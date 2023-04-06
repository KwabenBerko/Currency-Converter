package com.kwabenaberko.converter.factory

import app.cash.sqldelight.db.SqlDriver
import com.kwabenaberko.CurrencyConverterDatabase
import com.kwabenaberko.converter.data.network.HttpClientFactory
import com.kwabenaberko.converter.data.repository.RealCurrencyRepository
import com.kwabenaberko.converter.domain.repository.CurrencyRepository
import com.kwabenaberko.converter.domain.usecase.ConvertMoney
import com.kwabenaberko.converter.domain.usecase.GetCurrencies
import com.kwabenaberko.converter.domain.usecase.GetDefaultCurrencies
import com.kwabenaberko.converter.domain.usecase.GetRate
import com.kwabenaberko.converter.domain.usecase.GetSyncStatus
import com.kwabenaberko.converter.domain.usecase.HasCompletedInitialSync
import com.kwabenaberko.converter.domain.usecase.Sync
import com.kwabenaberko.converter.domain.usecase.implementation.RealConvertMoney
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

    internal val database: CurrencyConverterDatabase by lazy {
        return@lazy CurrencyConverterDatabase(sqlDriver)
    }

    private val httpClient: HttpClient by lazy {
        return@lazy HttpClientFactory.makeClient(httpClientEngine)
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
        return@lazy GetSyncStatus(currencyRepository::getSyncStatus)
    }

    val sync: Sync by lazy {
        return@lazy Sync(currencyRepository::sync)
    }

    val hasCompletedInitialSync by lazy {
        return@lazy HasCompletedInitialSync(currencyRepository::hasCompletedInitialSync)
    }

    val convertMoney: ConvertMoney by lazy {
        return@lazy RealConvertMoney(
            getRate = getRate,
            updateDefaultCurrencies = currencyRepository::updateDefaultCurrencies
        )
    }

    val getCurrencies: GetCurrencies by lazy {
        return@lazy GetCurrencies(currencyRepository::getCurrencies)
    }

    val getDefaultCurrencies: GetDefaultCurrencies by lazy {
        return@lazy GetDefaultCurrencies(currencyRepository::getDefaultCurrencies)
    }

    val getRate: GetRate by lazy {
        return@lazy GetRate(currencyRepository::getRate)
    }

    /**
     * This empty companion object allows library users to create a static extension
     * on the Container class
     */
    companion object
}
