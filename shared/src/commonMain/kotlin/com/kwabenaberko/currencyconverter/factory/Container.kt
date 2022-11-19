package com.kwabenaberko.currencyconverter.factory

import app.cash.sqldelight.db.SqlDriver
import com.kwabenaberko.CurrencyConverterDatabase
import com.kwabenaberko.currencyconverter.data.network.HttpClientFactory
import com.kwabenaberko.currencyconverter.data.repository.RealCurrencyRepository
import com.kwabenaberko.currencyconverter.domain.repository.CurrencyRepository
import com.kwabenaberko.currencyconverter.domain.usecase.ConvertMoney
import com.kwabenaberko.currencyconverter.domain.usecase.GetCurrencies
import com.kwabenaberko.currencyconverter.domain.usecase.GetDefaultCurrencies
import com.kwabenaberko.currencyconverter.domain.usecase.GetRate
import com.kwabenaberko.currencyconverter.domain.usecase.GetSyncStatus
import com.kwabenaberko.currencyconverter.domain.usecase.HasCompletedInitialSync
import com.kwabenaberko.currencyconverter.domain.usecase.Sync
import com.kwabenaberko.currencyconverter.domain.usecase.convertMoney
import com.russhwolf.settings.ObservableSettings
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import kotlinx.coroutines.CoroutineDispatcher

open class Container internal constructor(
    private val httpClientEngine: HttpClientEngine,
    private val sqlDriver: SqlDriver,
    private val settings: ObservableSettings,
    private val backgroundDispatcher: CoroutineDispatcher
) {

    internal val httpClient: HttpClient by lazy {
        return@lazy HttpClientFactory.makeClient(httpClientEngine)
    }

    internal val database: CurrencyConverterDatabase by lazy {
        return@lazy CurrencyConverterDatabase(sqlDriver)
    }

    private val currencyRepository: CurrencyRepository by lazy {
        return@lazy RealCurrencyRepository(
            httpClient = httpClient,
            currencyQueries = database.dbCurrencyQueries,
            exchangeRateQueries = database.dbExchangeRateQueries,
            settings = settings,
            backgroundDispatcher = backgroundDispatcher
        )
    }

    val getSyncStatus: GetSyncStatus by lazy {
        return@lazy GetSyncStatus(currencyRepository::syncStatus)
    }

    val sync: Sync by lazy {
        return@lazy Sync(currencyRepository::sync)
    }

    val hasCompletedInitialSync by lazy {
        return@lazy HasCompletedInitialSync(currencyRepository::hasCompletedInitialSync)
    }

    val convertMoney: ConvertMoney by lazy {
        return@lazy ConvertMoney { money, targetCurrency ->
            convertMoney(
                getRate = getRate,
                setDefaultCurrencies = currencyRepository::setDefaultCurrencies,
                money = money,
                targetCurrency = targetCurrency
            )
        }
    }

    val getCurrencies: GetCurrencies by lazy {
        return@lazy GetCurrencies(currencyRepository::currencies)
    }

    val getDefaultCurrencies: GetDefaultCurrencies by lazy {
        return@lazy GetDefaultCurrencies(currencyRepository::getDefaultCurrencies)
    }

    val getRate: GetRate by lazy {
        return@lazy GetRate(currencyRepository::getRate)
    }
}
