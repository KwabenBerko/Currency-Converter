package com.kwabenaberko.converter.repository

import app.cash.turbine.test
import com.kwabenaberko.CurrencyConverterDatabase
import com.kwabenaberko.converter.TestSqlDriverFactory
import com.kwabenaberko.converter.data.Api
import com.kwabenaberko.converter.data.Settings
import com.kwabenaberko.converter.data.network.HttpClientFactory
import com.kwabenaberko.converter.data.repository.RealCurrencyRepository
import com.kwabenaberko.converter.database.DbCurrency
import com.kwabenaberko.converter.database.DbExchangeRate
import com.kwabenaberko.converter.domain.model.DefaultCurrencies
import com.kwabenaberko.converter.domain.model.SyncStatus
import com.kwabenaberko.converter.builder.CurrencyFactory.makeCediCurrency
import com.kwabenaberko.converter.builder.CurrencyFactory.makeDollarCurrency
import com.kwabenaberko.converter.builder.CurrencyFactory.makeEuroCurrency
import com.kwabenaberko.converter.builder.CurrencyFactory.makeNairaCurrency
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.MapSettings
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher
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
            insert(DbCurrency(USD.code, USD.name, USD.symbol))
            insert(DbCurrency(GHS.code, GHS.name, GHS.symbol))
            insert(DbCurrency(NGN.code, NGN.name, NGN.symbol))
        }

        val sut = createCurrencyRepository(
            backgroundDispatcher = createTestDispatcher(testScheduler)
        )

        sut.getCurrencies(filter = null).test {
            awaitItem().shouldContainExactly(listOf(GHS, NGN, USD))
        }
    }

    @Test
    fun `should return an empty list if no match is found for filter`() = runTest {
        with(database.dbCurrencyQueries) {
            insert(DbCurrency(USD.code, USD.name, USD.symbol))
        }

        val sut = createCurrencyRepository(
            backgroundDispatcher = createTestDispatcher(testScheduler)
        )

        sut.getCurrencies(filter = "abcde").test {
            assertEquals(emptyList(), awaitItem())
        }
    }

    @Test
    fun `should return currencies in a sorted order if a match is found for filter`() = runTest {
        with(database.dbCurrencyQueries) {
            insert(DbCurrency(USD.code, USD.name, USD.symbol))
            insert(DbCurrency(GHS.code, GHS.name, GHS.symbol))
            insert(DbCurrency(NGN.code, NGN.name, NGN.symbol))
        }

        val sut = createCurrencyRepository(
            backgroundDispatcher = createTestDispatcher(testScheduler)
        )

        sut.getCurrencies(filter = "e").test {
            awaitItem().shouldContainExactly(listOf(GHS, NGN, USD))
        }
    }

    @Test
    fun `should set default currencies`() = runTest {
        with(database.dbCurrencyQueries) {
            insert(DbCurrency(GHS.code, GHS.name, GHS.symbol))
            insert(DbCurrency(NGN.code, NGN.name, NGN.symbol))
        }

        val sut = createCurrencyRepository(
            backgroundDispatcher = createTestDispatcher(testScheduler)
        )

        sut.updateDefaultCurrencies(GHS.code, NGN.code)
        sut.getDefaultCurrencies().test {
            assertEquals(DefaultCurrencies(GHS, NGN), awaitItem())
        }
    }

    @Test
    fun `should return USD and GHS as default currencies if no default currencies have been set`() =
        runTest {
            with(database.dbCurrencyQueries) {
                insert(DbCurrency(USD.code, USD.name, USD.symbol))
                insert(DbCurrency(GHS.code, GHS.name, GHS.symbol))
                insert(DbCurrency(NGN.code, NGN.name, NGN.symbol))
            }

            val sut = createCurrencyRepository(
                backgroundDispatcher = createTestDispatcher(testScheduler)
            )

            sut.getDefaultCurrencies().test {
                assertEquals(DefaultCurrencies(USD, GHS), awaitItem())
            }
        }

    @Test
    fun `should return rate for base and target codes`() = runTest {
        with(database.dbCurrencyQueries) {
            insert(DbCurrency(USD.code, USD.name, USD.symbol))
            insert(DbCurrency(GHS.code, GHS.name, GHS.symbol))
            insert(DbCurrency(NGN.code, NGN.name, NGN.symbol))
        }

        with(database.dbExchangeRateQueries) {
            insert(DbExchangeRate(USD.code, GHS.code, 10.015024))
            insert(DbExchangeRate(GHS.code, NGN.code, 42.235564))
        }

        val sut = createCurrencyRepository(
            backgroundDispatcher = createTestDispatcher(testScheduler)
        )

        forAll(
            table(
                headers("baseCode", "targetCode", "expectedRate"),
                row(USD.code, GHS.code, 10.015024),
                row(GHS.code, NGN.code, 42.235564)
            ),
        ) { baseCode, targetCode, expectedRate ->
            sut.getRate(baseCode, targetCode).test {
                assertEquals(expectedRate, awaitItem())
            }
        }
    }

    @Test
    fun `should fail when retrieving currencies`() = runTest {
        val mockEngine = MockEngine.create {
            addHandler { respondError(status = HttpStatusCode.NotFound) }
        }
        val sut = createCurrencyRepository(
            mockEngine = mockEngine,
            backgroundDispatcher = createTestDispatcher(testScheduler)
        )

        sut.getSyncStatus().test {
            assertEquals(null, awaitItem())

            val result = sut.sync()

            assertEquals(SyncStatus.InProgress, awaitItem())
            assertEquals(SyncStatus.Error, awaitItem())
            assertFalse(result)
        }
    }

    @Test
    fun `should fail if network connection is unavailable when retrieving currencies`() =
        runTest {
            val mockEngine = MockEngine.create {
                addHandler { throw IOException("") }
            }
            val sut = createCurrencyRepository(
                mockEngine = mockEngine,
                backgroundDispatcher = createTestDispatcher(testScheduler)
            )

            sut.getSyncStatus().test {
                assertEquals(null, awaitItem())

                val result = sut.sync()

                assertEquals(SyncStatus.InProgress, awaitItem())
                assertEquals(SyncStatus.Error, awaitItem())
                assertFalse(result)
            }
        }

    @Test
    fun `should fail when retrieving exchange rates`() = runTest {
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
        val sut = createCurrencyRepository(
            mockEngine = mockEngine,
            backgroundDispatcher = createTestDispatcher(testScheduler)
        )

        sut.getSyncStatus().test {
            assertEquals(null, awaitItem())

            val result = sut.sync()

            assertEquals(SyncStatus.InProgress, awaitItem())
            assertEquals(SyncStatus.Error, awaitItem())
            assertFalse(result)
        }
    }

    @Test
    fun `should fail if network connection is unavailable when retrieving exchange rates`() =
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
            val sut = createCurrencyRepository(
                mockEngine = mockEngine,
                backgroundDispatcher = createTestDispatcher(testScheduler)
            )

            sut.getSyncStatus().test {
                assertEquals(null, awaitItem())

                val result = sut.sync()

                assertEquals(SyncStatus.InProgress, awaitItem())
                assertEquals(SyncStatus.Error, awaitItem())
                assertFalse(result)
            }
        }

    @Test
    fun `should fail when retrieving currency symbols`() = runTest {
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
        val sut = createCurrencyRepository(
            mockEngine = mockEngine,
            backgroundDispatcher = createTestDispatcher(testScheduler)
        )

        sut.getSyncStatus().test {
            assertEquals(null, awaitItem())

            val result = sut.sync()

            assertEquals(SyncStatus.InProgress, awaitItem())
            assertEquals(SyncStatus.Error, awaitItem())
            assertFalse(result)
        }
    }

    @Test
    fun `should fail if network connection is unavailable when retrieving currency symbols`() =
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
            val sut = createCurrencyRepository(
                mockEngine = mockEngine,
                backgroundDispatcher = createTestDispatcher(testScheduler)
            )

            sut.getSyncStatus().test {
                assertEquals(null, awaitItem())

                val result = sut.sync()

                assertEquals(SyncStatus.InProgress, awaitItem())
                assertEquals(SyncStatus.Error, awaitItem())
                assertFalse(result)
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
            val sut = createCurrencyRepository(
                mockEngine = mockEngine,
                backgroundDispatcher = createTestDispatcher(testScheduler)
            )

            sut.getSyncStatus().test {
                assertEquals(null, awaitItem())

                val result = sut.sync()

                assertEquals(SyncStatus.InProgress, awaitItem())
                assertEquals(SyncStatus.Success, awaitItem())
                assertTrue(result)
            }
        }

    @Test
    fun `should save currencies and correctly convert rates when sync is successful`() =
        runTest {

            with(database.dbCurrencyQueries) {
                insert(DbCurrency(USD.code, USD.name, USD.symbol))
                insert(DbCurrency(GHS.code, GHS.name, GHS.symbol))
                insert(DbCurrency(NGN.code, NGN.name, NGN.symbol))
            }

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
            val sut = createCurrencyRepository(
                mockEngine = mockEngine,
                backgroundDispatcher = createTestDispatcher(testScheduler)
            )

            sut.sync()

            sut.getCurrencies(filter = null).test {
                awaitItem().shouldContainExactlyInAnyOrder(listOf(USD, NGN, GHS))
            }
            sut.getRate(USD.code, GHS.code).test {
                assertEquals(10.015024, awaitItem())
            }
            sut.getRate(GHS.code, USD.code).test {
                assertEquals(0.099850, awaitItem())
            }
            sut.getRate(GHS.code, NGN.code).test {
                assertEquals(42.235564, awaitItem())
            }
        }

    @Test
    fun `should update existing records when sync is successful`() = runTest {
        with(database.dbCurrencyQueries) {
            insert(DbCurrency(USD.code, "", USD.symbol))
            insert(DbCurrency(GHS.code, GHS.name, ""))
            insert(DbCurrency(NGN.code, NGN.name, NGN.symbol))
        }

        with(database.dbExchangeRateQueries) {
            insert(DbExchangeRate(USD.code, GHS.code, 0.0))
        }

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

        val sut = createCurrencyRepository(
            mockEngine = mockEngine,
            backgroundDispatcher = createTestDispatcher(testScheduler)
        )

        sut.sync()

        sut.getCurrencies(filter = null).test {
            awaitItem().shouldContainExactlyInAnyOrder(listOf(USD, GHS, NGN))
        }
        sut.getRate(USD.code, GHS.code).test {
            assertEquals(10.015024, awaitItem())
        }
    }

    @Test
    fun `should delete stale records when sync is successful`() = runTest {
        with(database.dbCurrencyQueries) {
            insert(DbCurrency(USD.code, USD.name, USD.symbol))
            insert(DbCurrency(GHS.code, GHS.name, GHS.symbol))
            insert(DbCurrency(NGN.code, NGN.name, NGN.symbol))
            insert(DbCurrency(EUR.code, EUR.name, EUR.symbol))
        }
        with(database.dbExchangeRateQueries) {
            insert(DbExchangeRate(USD.code, EUR.code, 0.0))
        }

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
        val sut = createCurrencyRepository(
            mockEngine = mockEngine,
            backgroundDispatcher = createTestDispatcher(testScheduler)
        )

        sut.sync()

        sut.getCurrencies(filter = null).test {
            awaitItem().shouldContainExactlyInAnyOrder(listOf(USD, NGN, GHS))
        }
        sut.getRate(USD.code, EUR.code).test {
            expectNoEvents()
        }
    }

    @Test
    fun `should return false if there has not been an initial sync`() = runTest {
        val sut = createCurrencyRepository(
            backgroundDispatcher = createTestDispatcher(testScheduler)
        )

        sut.hasCompletedInitialSync().test {
            assertFalse(awaitItem())
        }
    }

    @Test
    fun `should return true if the initial sync has been completed`() = runTest {
        val settings = MapSettings().apply {
            putLong(Settings.CURRENCIES_LAST_SYNC_DATE, 1662649992000)
        }
        val sut = createCurrencyRepository(
            settings = settings,
            backgroundDispatcher = createTestDispatcher(testScheduler)
        )

        sut.hasCompletedInitialSync().test {
            assertTrue(awaitItem())
        }
    }

    private val defaultHeaders = headersOf(
        HttpHeaders.ContentType, ContentType.Application.Json.toString()
    )

    private val defaultEngine = MockEngine.create {
        addHandler { respondOk() }
    }

    private fun createTestDispatcher(scheduler: TestCoroutineScheduler): TestDispatcher {
        return StandardTestDispatcher(scheduler)
    }

    private fun createCurrencyRepository(
        mockEngine: HttpClientEngine = defaultEngine,
        settings: MapSettings = MapSettings(),
        backgroundDispatcher: CoroutineDispatcher
    ): RealCurrencyRepository {
        return RealCurrencyRepository(
            httpClient = HttpClientFactory.makeClient(mockEngine),
            currencyQueries = database.dbCurrencyQueries,
            exchangeRateQueries = database.dbExchangeRateQueries,
            settings = settings,
            backgroundDispatcher = backgroundDispatcher
        )
    }

    private companion object {
        val USD = makeDollarCurrency()
        val GHS = makeCediCurrency()
        val NGN = makeNairaCurrency()
        val EUR = makeEuroCurrency()

        //language=JSON
        val CURRENCIES_JSON = """
            {
              "symbols": {
                "${GHS.code}": {
                  "code": "${GHS.code}",
                  "description": "${GHS.name}"
                },
                "${NGN.code}": {
                  "code": "${NGN.code}",
                  "description": "${NGN.name}"
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
