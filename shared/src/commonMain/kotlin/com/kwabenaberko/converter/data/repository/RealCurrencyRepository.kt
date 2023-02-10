package com.kwabenaberko.converter.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import com.kwabenaberko.converter.data.Api
import com.kwabenaberko.converter.data.Settings
import com.kwabenaberko.converter.data.network.dto.CurrenciesDto
import com.kwabenaberko.converter.data.network.dto.CurrencySymbolDto
import com.kwabenaberko.converter.data.network.dto.ExchangeRatesDto
import com.kwabenaberko.converter.database.DbCurrency
import com.kwabenaberko.converter.database.DbCurrencyQueries
import com.kwabenaberko.converter.database.DbExchangeRate
import com.kwabenaberko.converter.database.DbExchangeRateQueries
import com.kwabenaberko.converter.domain.model.Currency
import com.kwabenaberko.converter.domain.model.DefaultCurrencies
import com.kwabenaberko.converter.domain.model.SyncStatus
import com.kwabenaberko.converter.domain.repository.CurrencyRepository
import com.kwabenaberko.converter.toPlaces
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.getLongOrNullFlow
import com.russhwolf.settings.coroutines.getStringFlow
import com.russhwolf.settings.coroutines.getStringOrNullFlow
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.isSuccess
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlin.collections.component1
import kotlin.collections.component2

@OptIn(ExperimentalSettingsApi::class, ExperimentalCoroutinesApi::class)
class RealCurrencyRepository(
    private val httpClient: HttpClient,
    private val currencyQueries: DbCurrencyQueries,
    private val exchangeRateQueries: DbExchangeRateQueries,
    private val settings: ObservableSettings,
    private val backgroundDispatcher: CoroutineDispatcher
) : CurrencyRepository {

    override fun getCurrencies(filter: String?): Flow<List<Currency>> {
        return currencyQueries
            .selectAllCurrencies(filter = filter?.trim() ?: "")
            .asFlow()
            .mapToList(backgroundDispatcher)
            .flowOn(backgroundDispatcher)
            .map { dbCurrencies ->
                mapDbCurrenciesToDomain(dbCurrencies)
                    .sortedBy { currency -> currency.name }
            }

    }

    override fun getDefaultCurrencies(): Flow<DefaultCurrencies> {
        val baseCodeFlow = settings.getStringFlow(Settings.BASE_CODE, "USD")
        val targetCodeFlow = settings.getStringFlow(Settings.TARGET_CODE, "GHS")

        return combine(baseCodeFlow, targetCodeFlow) { baseCode, targetCode ->
            val baseCurrency = currencyQueries
                .selectCurrencyByCode(baseCode)
                .executeAsOne()

            val targetCurrency = currencyQueries
                .selectCurrencyByCode(targetCode)
                .executeAsOne()

            DefaultCurrencies(
                base = mapDbCurrencyToDomain(baseCurrency),
                target = mapDbCurrencyToDomain(targetCurrency)
            )

        }.flowOn(backgroundDispatcher)
    }

    override suspend fun updateDefaultCurrencies(baseCode: String, targetCode: String) {
        withContext(backgroundDispatcher) {
            with(settings) {
                putString(Settings.BASE_CODE, baseCode)
                putString(Settings.TARGET_CODE, targetCode)
            }
        }
    }

    override fun getRate(baseCode: String, targetCode: String): Flow<Double> {
        val isRateAvailableFlow = exchangeRateQueries
            .isRateAvailable(baseCode, targetCode)
            .asFlow()
            .mapToOne(backgroundDispatcher)

        return isRateAvailableFlow
            .take(1)
            .flatMapLatest { isRateAvailable ->
                if (!isRateAvailable) {
                    val usdRates = exchangeRateQueries
                        .selectRatesForCurrency(USD_RATE)
                        .executeAsList()
                        .associateBy { dbExchangeRate -> dbExchangeRate.targetCode }

                    val usdToBaseCodeRate = usdRates.getValue(baseCode).rate
                    val rate = if (targetCode.equals(USD_RATE, ignoreCase = true)) {
                        1.0.div(usdToBaseCodeRate).toPlaces(DECIMAL_PLACES)
                    } else {
                        val usdToTargetCodeRate = usdRates.getValue(targetCode).rate
                        1.0.div(usdToBaseCodeRate).times(usdToTargetCodeRate)
                            .toPlaces(DECIMAL_PLACES)
                    }

                    val dbExchangeRate = DbExchangeRate(baseCode, targetCode, rate)
                    exchangeRateQueries.insertOrUpdate(dbExchangeRate)
                }

                exchangeRateQueries
                    .selectRateForCurrencies(baseCode, targetCode)
                    .asFlow()
                    .mapToOne(backgroundDispatcher)
                    .flowOn(backgroundDispatcher)
                    .map { dbExchangeRate -> dbExchangeRate.rate }
            }
    }

    override fun getSyncStatus(): Flow<SyncStatus?> {
        return settings.getStringOrNullFlow(Settings.CURRENCIES_SYNC_STATUS)
            .flowOn(backgroundDispatcher)
            .map { encodedStatus ->
                encodedStatus?.let { SyncStatus.valueOf(encodedStatus) }
            }
    }

    override fun hasCompletedInitialSync(): Flow<Boolean> {
        return settings.getLongOrNullFlow(Settings.CURRENCIES_LAST_SYNC_DATE)
            .map { lastSyncDate -> lastSyncDate != null }
            .flowOn(backgroundDispatcher)
    }

    override suspend fun sync(): Boolean {
        settings.putString(Settings.CURRENCIES_SYNC_STATUS, SyncStatus.InProgress.name)
        return try {
            withContext(backgroundDispatcher) {
                val responses = awaitAll(
                    async { httpClient.get(Api.CURRENCIES) },
                    async { httpClient.get(Api.CURRENCY_SYMBOLS_URL) },
                    async {
                        httpClient.get(Api.EXCHANGE_RATES) {
                            parameter("base", USD_RATE)
                        }
                    }
                )
                if (responses.any { response -> !response.status.isSuccess() }) {
                    settings.putString(Settings.CURRENCIES_SYNC_STATUS, SyncStatus.Error.name)
                    return@withContext false
                }

                val (currenciesResponse, symbolsResponse, exchangeRatesResponse) = responses
                val currencies = currenciesResponse.body<CurrenciesDto>().currencies
                val symbols = symbolsResponse.body<Map<String, CurrencySymbolDto>>()
                val (baseCode, baseCodeRates) = exchangeRatesResponse.body<ExchangeRatesDto>()

                val dbCurrencies = currencies.map { (_ , currency) ->
                    val symbol = symbols[currency.code]?.symbol ?: currency.code
                    DbCurrency(currency.code, currency.name, symbol)
                }
                val dbExchangeRates = baseCodeRates.map { (targetCode, rate) ->
                    DbExchangeRate(baseCode, targetCode, rate)
                }
                val staleCurrencyCodes = currencyQueries
                    .selectAllCurrencies(filter = "")
                    .executeAsList()
                    .map { dbCurrency -> dbCurrency.code }
                    .filter { code ->
                        code !in dbCurrencies.map { newDbCurrency -> newDbCurrency.code}
                    }

                currencyQueries.transaction {

                    currencyQueries.deleteAllCurrenciesByCode(staleCurrencyCodes)

                    dbCurrencies.forEach { dbCurrency ->
                        currencyQueries.insertOrUpdate(dbCurrency)
                    }

                    dbExchangeRates.forEach { dbExchangeRate ->
                        exchangeRateQueries.insertOrUpdate(dbExchangeRate)
                    }
                }

                settings.putLong(
                    key = Settings.CURRENCIES_LAST_SYNC_DATE,
                    value = Clock.System.now().toEpochMilliseconds()
                )
                settings.putString(Settings.CURRENCIES_SYNC_STATUS, SyncStatus.Success.name)
                return@withContext true
            }
        } catch (exception: IOException) {
            settings.putString(Settings.CURRENCIES_SYNC_STATUS, SyncStatus.Error.name)
            return false
        }
    }

    private fun mapDbCurrenciesToDomain(dbCurrencies: List<DbCurrency>): List<Currency> {
        return dbCurrencies.map { dbCurrency -> mapDbCurrencyToDomain(dbCurrency) }
    }

    private fun mapDbCurrencyToDomain(dbCurrency: DbCurrency): Currency {
        return Currency(dbCurrency.code, dbCurrency.name, dbCurrency.symbol)
    }

    private fun DbCurrencyQueries.insertOrUpdate(dbCurrency: DbCurrency) {
        try {
            this.insert(dbCurrency)
        } catch (throwable: Throwable) {
            val (code, name, symbol) = dbCurrency
            this.update(name, symbol, code)
        }
    }

    private fun DbExchangeRateQueries.insertOrUpdate(dbExchangeRate: DbExchangeRate) {
        try {
            this.insert(dbExchangeRate)
        } catch (throwable: Throwable) {
            val (baseCode, targetCode, rate) = dbExchangeRate
            this.update(rate, baseCode, targetCode)
        }
    }

    private companion object {
        const val USD_RATE = "USD"
        const val DECIMAL_PLACES = 6
    }
}
