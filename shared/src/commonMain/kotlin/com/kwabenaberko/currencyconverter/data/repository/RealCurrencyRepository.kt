package com.kwabenaberko.currencyconverter.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.kwabenaberko.currencyconverter.data.Api
import com.kwabenaberko.currencyconverter.data.Settings
import com.kwabenaberko.currencyconverter.data.network.dto.CurrenciesDto
import com.kwabenaberko.currencyconverter.data.network.dto.CurrencySymbolDto
import com.kwabenaberko.currencyconverter.data.network.dto.ExchangeRatesDto
import com.kwabenaberko.currencyconverter.database.DbCurrency
import com.kwabenaberko.currencyconverter.database.DbCurrencyQueries
import com.kwabenaberko.currencyconverter.database.DbExchangeRateQueries
import com.kwabenaberko.currencyconverter.domain.model.Currency
import com.kwabenaberko.currencyconverter.domain.model.DefaultCurrencies
import com.kwabenaberko.currencyconverter.domain.model.SyncStatus
import com.kwabenaberko.currencyconverter.domain.repository.CurrencyRepository
import com.kwabenaberko.currencyconverter.toPlaces
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
import kotlin.collections.set

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
            .selectAllCurrencies(filter = filter ?: "")
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
            exchangeRateQueries
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
                            parameter("base", "USD")
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
                val exchangeRates = calculateExchangeRates(baseCode, baseCodeRates)

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
                    exchangeRates.forEach { (baseCode, rates) ->
                        rates.forEach { (targetCode, rate) ->
                            exchangeRateQueries.insert(baseCode, targetCode, rate)
                        }
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

    private fun calculateExchangeRates(
        baseCode: String,
        baseCodeRates: Map<String, Double>
    ): Map<String, Map<String, Double>> {
        val exchangeRates = mutableMapOf(baseCode to baseCodeRates)

        baseCodeRates.forEach { (code, rate) ->
            val currentCodeRates = mutableMapOf(baseCode to 1.0.div(rate).toPlaces(DECIMAL_PLACES))
            exchangeRates[code] = currentCodeRates
            baseCodeRates.forEach { entry ->
                currentCodeRates[entry.key] = 1.0
                    .div(rate)
                    .times(entry.value)
                    .toPlaces(places = DECIMAL_PLACES)
            }
        }

        return exchangeRates
    }

    private fun mapDbCurrenciesToDomain(dbCurrencies: List<DbCurrency>): List<Currency> {
        return dbCurrencies.map { dbCurrency -> mapDbCurrencyToDomain(dbCurrency) }
    }

    private fun mapDbCurrencyToDomain(dbCurrency: DbCurrency): Currency {
        return Currency(dbCurrency.code, dbCurrency.name, dbCurrency.symbol)
    }

    private companion object {
        const val DECIMAL_PLACES = 6
    }
}
