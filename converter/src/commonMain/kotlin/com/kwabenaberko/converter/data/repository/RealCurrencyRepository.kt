package com.kwabenaberko.converter.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.kwabenaberko.converter.data.Api
import com.kwabenaberko.converter.data.Settings
import com.kwabenaberko.converter.data.network.dto.CurrenciesDto
import com.kwabenaberko.converter.data.network.dto.CurrencySymbolDto
import com.kwabenaberko.converter.data.network.dto.ExchangeRatesDto
import com.kwabenaberko.converter.database.DbCurrency
import com.kwabenaberko.converter.database.DbCurrencyQueries
import com.kwabenaberko.converter.database.DbExchangeRateQueries
import com.kwabenaberko.converter.domain.model.Currency
import com.kwabenaberko.converter.domain.model.DefaultCurrencies
import com.kwabenaberko.converter.domain.model.SyncStatus
import com.kwabenaberko.converter.domain.repository.CurrencyRepository
import com.kwabenaberko.converter.toPlaces
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.getStringOrNullFlow
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.isSuccess
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlin.collections.component1
import kotlin.collections.component2

@OptIn(ExperimentalSettingsApi::class)
class RealCurrencyRepository(
    private val httpClient: HttpClient,
    private val currencyQueries: DbCurrencyQueries,
    private val exchangeRateQueries: DbExchangeRateQueries,
    private val settings: ObservableSettings,
    private val backgroundDispatcher: CoroutineDispatcher
) : CurrencyRepository {

    override fun currencies(filter: String?): Flow<List<Currency>> {
        return currencyQueries
            .selectAllCurrencies(filter = filter?.trim() ?: "")
            .asFlow()
            .flowOn(backgroundDispatcher)
            .mapToList(backgroundDispatcher)
            .map { dbCurrencies ->
                mapDbCurrenciesToDomain(dbCurrencies)
                    .sortedBy { currency -> currency.name }
            }

    }

    override suspend fun getDefaultCurrencies(): DefaultCurrencies {
        return withContext(backgroundDispatcher) {
            val baseCode = settings.getString(Settings.BASE_CODE, "USD")
            val targetCode = settings.getString(Settings.TARGET_CODE, "GHS")

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
        }
    }

    override suspend fun setDefaultCurrencies(baseCode: String, targetCode: String) {
        withContext(backgroundDispatcher) {
            with(settings) {
                putString(Settings.BASE_CODE, baseCode)
                putString(Settings.TARGET_CODE, targetCode)
            }
        }
    }

    override suspend fun getRate(baseCode: String, targetCode: String): Double {
        return withContext(backgroundDispatcher) {
            val isRateAvailable = exchangeRateQueries
                .isRateAvailable(baseCode, targetCode)
                .executeAsOne()

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
                    1.0.div(usdToBaseCodeRate).times(usdToTargetCodeRate).toPlaces(DECIMAL_PLACES)
                }

                exchangeRateQueries.insert(
                    baseCode = baseCode,
                    targetCode = targetCode,
                    rate = rate
                )
            }

            return@withContext exchangeRateQueries
                .selectRateForCurrencies(baseCode, targetCode)
                .executeAsOne()
                .rate
        }
    }

    override fun syncStatus(): Flow<SyncStatus?> {
        return settings.getStringOrNullFlow(Settings.CURRENCIES_SYNC_STATUS)
            .flowOn(backgroundDispatcher)
            .map { encodedStatus ->
                encodedStatus?.let { SyncStatus.valueOf(encodedStatus) }
            }
    }

    override suspend fun hasCompletedInitialSync(): Boolean {
        return withContext(backgroundDispatcher) {
            settings.hasKey(Settings.CURRENCIES_LAST_SYNC_DATE)
        }
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

                currencyQueries.transaction {
                    currencies.forEach { (_, currency) ->
                        val symbol = symbols[currency.code]?.symbol ?: currency.code
                        currencyQueries.insert(
                            code = currency.code,
                            name = currency.name,
                            symbol = symbol
                        )
                    }
                }

                exchangeRateQueries.transaction {
                    baseCodeRates.forEach { (targetCode, rate) ->
                        exchangeRateQueries.insert(baseCode, targetCode, rate)
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

    private companion object {
        const val USD_RATE = "USD"
        const val DECIMAL_PLACES = 6
    }
}
