package com.kwabenaberko.currencyconverter.repository

import app.cash.turbine.test
import com.kwabenaberko.CurrencyConverterDatabase
import com.kwabenaberko.currencyconverter.TestSqlDriverFactory
import com.kwabenaberko.currencyconverter.data.Api
import com.kwabenaberko.currencyconverter.data.Settings
import com.kwabenaberko.currencyconverter.data.network.HttpClientFactory
import com.kwabenaberko.currencyconverter.data.repository.RealCurrencyRepository
import com.kwabenaberko.currencyconverter.domain.model.CurrencyFilter
import com.kwabenaberko.currencyconverter.domain.model.DefaultCurrencies
import com.kwabenaberko.currencyconverter.domain.model.SyncStatus
import com.kwabenaberko.sharedtest.builder.CurrencyFactory.makeCediCurrency
import com.kwabenaberko.sharedtest.builder.CurrencyFactory.makeDollarCurrency
import com.kwabenaberko.sharedtest.builder.CurrencyFactory.makeNairaCurrency
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.MapSettings
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.client.engine.mock.respondOk
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExperimentalSettingsApi
@ExperimentalCoroutinesApi
class RealCurrencyRepositoryTest {

    private val sqlDriver = TestSqlDriverFactory().create()
    private val database = CurrencyConverterDatabase(sqlDriver)

    @AfterTest
    fun teardown() {
        sqlDriver.close()
    }

    @Test
    fun `should return currencies in sorted order`() = runTest {
        with(database.dbCurrencyQueries) {
            insert(USD.code, USD.name, USD.symbol)
            insert(GHS.code, GHS.name, GHS.symbol)
        }
        val sut = createCurrencyRepository()

        sut.currencies(filter = null).test {
            assertEquals(listOf(GHS, USD), awaitItem())
        }
    }

    @Test
    fun `should return an empty list if no match is found for filter`() = runTest {
        with(database.dbCurrencyQueries) {
            insert(USD.code, USD.name, USD.symbol)
            insert(GHS.code, GHS.name, GHS.symbol)
        }

        val sut = createCurrencyRepository()

        sut.currencies(CurrencyFilter(name = "abcde")).test {
            assertEquals(emptyList(), awaitItem())
        }
    }

    @Test
    fun `should return currencies in a sorted order if a match is found for filter`() = runTest {
        with(database.dbCurrencyQueries) {
            insert(USD.code, USD.name, USD.symbol)
            insert(GHS.code, GHS.name, GHS.symbol)
        }

        val sut = createCurrencyRepository()

        sut.currencies(CurrencyFilter(name = "e")).test {
            assertEquals(listOf(GHS, USD), awaitItem())
        }
    }

    @Test
    fun `should set default currencies`() = runTest {
        with(database.dbCurrencyQueries) {
            insert(USD.code, USD.name, USD.symbol)
            insert(GHS.code, GHS.name, GHS.symbol)
            insert(NGN.code, NGN.name, NGN.symbol)
        }
        val sut = createCurrencyRepository()

        sut.setDefaultCurrencies(GHS.code, NGN.code)

        sut.defaultCurrencies().test {
            assertEquals(DefaultCurrencies(GHS, NGN), awaitItem())
        }
    }

    @Test
    fun `should return USD and GHS as default currencies if no default currencies have been set`() =
        runTest {
            with(database.dbCurrencyQueries) {
                insert(USD.code, USD.name, USD.symbol)
                insert(GHS.code, GHS.name, GHS.symbol)
            }
            val sut = createCurrencyRepository()

            sut.defaultCurrencies().test {
                assertEquals(DefaultCurrencies(USD, GHS), awaitItem())
            }
        }

    @Test
    fun `should return rate for base and target codes`() = runTest {
        with(database.dbExchangeRateQueries) {
            insert(baseCode = USD.code, targetCode = GHS.code, rate = 10.015024)
            insert(baseCode = GHS.code, targetCode = NGN.code, rate = 42.235564)
        }
        val sut = createCurrencyRepository()

        forAll(
            table(
                headers("baseCode", "targetCode", "expectedRate"),
                row(USD.code, GHS.code, 10.015024),
                row(GHS.code, NGN.code, 42.235564)
            ),
        ) { baseCode, targetCode, expectedRate ->
            val rate = sut.getRate(baseCode, targetCode)
            assertEquals(expectedRate, rate)
        }
    }

    @Test
    fun `should return a sync error when retrieving currencies`() = runTest {
        val mockEngine = MockEngine.create {
            addHandler { respondError(status = HttpStatusCode.NotFound) }
        }
        val sut = createCurrencyRepository(mockEngine)

        sut.syncStatus().test {

            sut.sync()

            assertEquals(SyncStatus.InProgress, awaitItem())
            assertEquals(SyncStatus.Error, awaitItem())
        }
    }

    @Test
    fun `should return a sync error when network connection is unavailable when retrieving currencies`() =
        runTest {
            val mockEngine = MockEngine.create {
                addHandler { throw IOException("") }
            }
            val sut = createCurrencyRepository(mockEngine)

            sut.syncStatus().test {

                sut.sync()

                assertEquals(SyncStatus.InProgress, awaitItem())
                assertEquals(SyncStatus.Error, awaitItem())
            }
        }

    @Test
    fun `should return a sync error when retrieving exchange rates`() = runTest {
        val mockEngine = MockEngine.create {
            addHandler { request ->
                when {
                    request.url.encodedPath.contains(Api.CURRENCIES) -> {
                        respond(content = CURRENCIES_JSON, headers = defaultHeaders)
                    }
                    else -> respondError(status = HttpStatusCode.NotFound)
                }
            }
        }
        val sut = createCurrencyRepository(mockEngine)

        sut.syncStatus().test {

            sut.sync()

            assertEquals(SyncStatus.InProgress, awaitItem())
            assertEquals(SyncStatus.Error, awaitItem())
        }
    }

    @Test
    fun `should return a sync error if network connection is unavailable when retrieving exchange rates`() =
        runTest {
            val mockEngine = MockEngine.create {
                addHandler { request ->
                    when {
                        request.url.encodedPath.contains(Api.CURRENCIES) -> {
                            respond(content = CURRENCIES_JSON, headers = defaultHeaders)
                        }
                        else -> throw IOException("")
                    }
                }
            }
            val sut = createCurrencyRepository(mockEngine)

            sut.syncStatus().test {

                sut.sync()

                assertEquals(SyncStatus.InProgress, awaitItem())
                assertEquals(SyncStatus.Error, awaitItem())
            }
        }

    @Test
    fun `should return a sync error when retrieving currency symbols`() = runTest {
        val mockEngine = MockEngine.create {
            addHandler { request ->
                when {
                    request.url.encodedPath.contains(Api.CURRENCIES) -> {
                        respond(content = CURRENCIES_JSON, headers = defaultHeaders)
                    }
                    request.url.encodedPath.contains(Api.EXCHANGE_RATES) -> {
                        respond(content = EXCHANGE_RATES_JSON, headers = defaultHeaders)
                    }
                    else -> respondError(status = HttpStatusCode.NotFound)
                }
            }
        }
        val sut = createCurrencyRepository(mockEngine)

        sut.syncStatus().test {

            sut.sync()

            assertEquals(SyncStatus.InProgress, awaitItem())
            assertEquals(SyncStatus.Error, awaitItem())
        }
    }

    @Test
    fun `should return a sync error if network connection is unavailable when retrieving currency symbols`() =
        runTest {
            val mockEngine = MockEngine.create {
                addHandler { request ->
                    when {
                        request.url.encodedPath.contains(Api.CURRENCIES) -> {
                            respond(content = CURRENCIES_JSON, headers = defaultHeaders)
                        }
                        request.url.encodedPath.contains(Api.EXCHANGE_RATES) -> {
                            respond(content = EXCHANGE_RATES_JSON, headers = defaultHeaders)
                        }
                        else -> throw IOException("")
                    }
                }
            }
            val sut = createCurrencyRepository(mockEngine)

            sut.syncStatus().test {

                sut.sync()

                assertEquals(SyncStatus.InProgress, awaitItem())
                assertEquals(SyncStatus.Error, awaitItem())
            }
        }

    @Test
    fun `should return a successful result when sync is successful`() =
        runTest {
            val mockEngine = MockEngine.create {
                addHandler { request ->
                    when {
                        request.url.encodedPath.contains(Api.CURRENCIES) -> {
                            respond(content = CURRENCIES_JSON, headers = defaultHeaders)
                        }
                        request.url.encodedPath.contains(Api.EXCHANGE_RATES) -> {
                            respond(content = EXCHANGE_RATES_JSON, headers = defaultHeaders)
                        }
                        else -> respond(content = CURRENCY_SYMBOLS_JSON, headers = defaultHeaders)
                    }
                }
            }
            val sut = createCurrencyRepository(mockEngine)

            sut.syncStatus().test {

                sut.sync()

                assertEquals(SyncStatus.InProgress, awaitItem())
                assertEquals(SyncStatus.Success, awaitItem())
            }
        }

    @Test
    fun `should save currencies and correctly convert rates when sync is successful`() =
        runTest {
            val mockEngine = MockEngine.create {
                addHandler { request ->
                    when {
                        request.url.encodedPath.contains(Api.CURRENCIES) -> {
                            respond(content = CURRENCIES_JSON, headers = defaultHeaders)
                        }
                        request.url.encodedPath.contains(Api.EXCHANGE_RATES) -> {
                            respond(content = EXCHANGE_RATES_JSON, headers = defaultHeaders)
                        }
                        else -> respond(content = CURRENCY_SYMBOLS_JSON, headers = defaultHeaders)
                    }
                }
            }
            val sut = createCurrencyRepository(mockEngine)

            sut.sync()

            sut.currencies(filter = null).test {
                assertTrue(awaitItem().containsAll(listOf(USD, GHS)))
            }
            assertEquals(
                expected = 10.015024,
                actual = sut.getRate(USD.code, GHS.code),
                absoluteTolerance = 0.01
            )
            assertEquals(
                expected = 0.09985,
                actual = sut.getRate(GHS.code, USD.code),
                absoluteTolerance = 0.01
            )
            assertEquals(
                expected = 42.235564,
                actual = sut.getRate(GHS.code, NGN.code),
                absoluteTolerance = 0.01
            )
        }

    @Test
    fun `should return false if there has not been an initial sync`() = runTest {
        val sut = createCurrencyRepository()

        val result = sut.hasCompletedInitialSync()

        assertFalse(result)
    }

    @Test
    fun `should return true if the initial sync has been completed`() = runTest {
        val settings = MapSettings().apply {
            putLong(Settings.CURRENCIES_LAST_SYNC_DATE, 1662649992000)
        }
        val sut = createCurrencyRepository(settings = settings)

        val result = sut.hasCompletedInitialSync()

        assertTrue(result)
    }

    private val defaultHeaders = headersOf(
        HttpHeaders.ContentType, ContentType.Application.Json.toString()
    )

    private val defaultEngine = MockEngine.create {
        addHandler { respondOk() }
    }
    private val defaultSettings = MapSettings()
    private fun createCurrencyRepository(
        mockEngine: HttpClientEngine = defaultEngine,
        settings: MapSettings = defaultSettings
    ): RealCurrencyRepository {
        return RealCurrencyRepository(
            httpClient = HttpClientFactory.makeClient(mockEngine),
            currencyQueries = database.dbCurrencyQueries,
            exchangeRateQueries = database.dbExchangeRateQueries,
            settings = settings,
            backgroundDispatcher = Dispatchers.Default
        )
    }

    private companion object {
        val USD = makeDollarCurrency()
        val GHS = makeCediCurrency()
        val NGN = makeNairaCurrency()

        //language=JSON
        val CURRENCIES_JSON = """
            {
              "symbols": {
                "${GHS.code}": {
                  "code": "${GHS.code}",
                  "description": "${GHS.name}"
                },
                "${USD.code}": {
                  "code": "${USD.code}",
                  "description": "${USD.name}"
                }
              }
            }
        """.trimIndent()

        //language=JSON
        val CURRENCY_SYMBOLS_JSON = """
            {
              "${GHS.code}": {
                "symbol": "${GHS.symbol}"
              },
              "${USD.code}": {
                "symbol": "${USD.symbol}"
              },
              "${NGN.code}": {
                "symbol": "${NGN.symbol}"
              }
            }
        """.trimIndent()

        //language=JSON
        val EXCHANGE_RATES_JSON = """
            {
              "base": "${USD.code}",
              "rates": {
                "${GHS.code}": 10.015024,
                "${NGN.code}": 422.990183
              }
            }
        """.trimIndent()
    }
}
