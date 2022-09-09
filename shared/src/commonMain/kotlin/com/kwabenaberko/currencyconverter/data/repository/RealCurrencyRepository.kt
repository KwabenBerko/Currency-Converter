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
import com.kwabenaberko.currencyconverter.round
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.getStringFlow
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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

@OptIn(ExperimentalSettingsApi::class)
class RealCurrencyRepository(
    private val httpClient: HttpClient,
    private val currencyQueries: DbCurrencyQueries,
    private val exchangeRateQueries: DbExchangeRateQueries,
    private val settings: ObservableSettings,
    private val backgroundDispatcher: CoroutineDispatcher
) : CurrencyRepository {

    private val syncStatus = MutableSharedFlow<SyncStatus>()

    override fun currencies(): Flow<List<Currency>> {
        return currencyQueries
            .selectAllCurrencies()
            .asFlow()
            .flowOn(backgroundDispatcher)
            .mapToList(backgroundDispatcher)
            .map { dbCurrencies ->
                mapDbCurrenciesToDomain(dbCurrencies)
                    .sortedBy { currency -> currency.name }
            }
    }

    override fun defaultCurrencies(): Flow<DefaultCurrencies> {
        val baseCodeFlow = settings.getStringFlow(Settings.BASE_CODE, "USD")
        val targetCodeFlow = settings.getStringFlow(Settings.TARGET_CODE, "GHS")

        return baseCodeFlow.zip(targetCodeFlow) { baseCode, targetCode ->
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

    override suspend fun setDefaultCurrencies(baseCode: String, targetCode: String) {
        withContext(backgroundDispatcher) {
            with(settings) {
                putString(Settings.BASE_CODE, baseCode)
                putString(Settings.TARGET_CODE, targetCode)
            }
        }
    }

    override fun syncStatus(): Flow<SyncStatus> {
        return syncStatus.asSharedFlow()
    }

    override suspend fun hasCompletedInitialSync(): Boolean {
        return settings.hasKey(Settings.CURRENCIES_LAST_SYNC_DATE)
    }

    override suspend fun getRate(baseCode: String, targetCode: String): Double {
        return withContext(backgroundDispatcher) {
            exchangeRateQueries
                .selectRateForCurrencies(baseCode, targetCode)
                .executeAsOne()
                .rate
        }
    }

    override suspend fun sync() {
        try {
            syncStatus.emit(SyncStatus.InProgress)

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
                    syncStatus.emit(SyncStatus.Error)
                    return@withContext
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
                syncStatus.emit(SyncStatus.Success)
            }
        } catch (exception: IOException) {
            syncStatus.emit(SyncStatus.Error)
        }
    }

    private fun calculateExchangeRates(
        baseCode: String,
        baseCodeRates: Map<String, Double>
    ): Map<String, Map<String, Double>> {
        val exchangeRates = mutableMapOf(baseCode to baseCodeRates)

        baseCodeRates.forEach { (code, rate) ->
            val currentCodeRates = mutableMapOf(
                baseCode to 1.0.div(rate).round(places = DECIMAL_PLACES)
            )
            exchangeRates[code] = currentCodeRates
            baseCodeRates
                .filterKeys { key -> key != code }
                .forEach { entry ->
                    currentCodeRates[entry.key] = 1.0.div(rate)
                        .round(places = DECIMAL_PLACES)
                        .times(entry.value)
                        .round(places = DECIMAL_PLACES)
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
