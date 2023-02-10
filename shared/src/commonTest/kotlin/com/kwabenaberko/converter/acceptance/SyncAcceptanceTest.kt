package com.kwabenaberko.converter.acceptance

import app.cash.turbine.test
import app.cash.turbine.testIn
import com.kwabenaberko.converter.TestContainer
import com.kwabenaberko.converter.data.Api
import com.kwabenaberko.converter.domain.model.SyncStatus
import com.kwabenaberko.converter.builder.CurrencyFactory.makeCediCurrency
import com.kwabenaberko.converter.builder.CurrencyFactory.makeDollarCurrency
import com.kwabenaberko.converter.builder.CurrencyFactory.makeNairaCurrency
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.headersOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class SyncAcceptanceTest {

    private val container = TestContainer(httpClientEngine = makeHttpClientEngine())
    private val getCurrencies = container.getCurrencies
    private val getRate = container.getRate
    private val getSyncStatus = container.getSyncStatus
    private val hasCompletedInitialSync = container.hasCompletedInitialSync
    private val sut = container.sync

    @AfterTest
    fun teardown() {
        container.sqlDriver.close()
    }

    @Test
    fun `should successfully sync currencies`() = runTest {
        val statusObserver = getSyncStatus().testIn(this)
        val currenciesObserver = getCurrencies(null).testIn(this)

        assertEquals(null, statusObserver.awaitItem())
        assertEquals(emptyList(), currenciesObserver.awaitItem())

        sut.invoke()

        with(statusObserver) {
            assertEquals(SyncStatus.InProgress, awaitItem())
            assertEquals(SyncStatus.Success, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        with(currenciesObserver) {
            assertTrue(awaitItem().containsAll(listOf(GHS, USD, NGN)))
            cancelAndIgnoreRemainingEvents()
        }

        forAll(
            table(
                headers("baseCode", "targetCode", "rate"),
                row(USD.code, GHS.code, 10.015024),
                row(GHS.code, USD.code, 0.099850),
                row(USD.code, NGN.code, 422.990183),
                row(NGN.code, USD.code, 0.002364),
                row(GHS.code, NGN.code, 42.235564),
                row(NGN.code, GHS.code, 0.023677),
            )
        ) { baseCode, targetCode, rate ->
            getRate(baseCode, targetCode).test {
                assertEquals(rate, awaitItem())
            }
            hasCompletedInitialSync().test {
                assertTrue(awaitItem())
            }
        }
    }

    private fun makeHttpClientEngine(): HttpClientEngine {
        return MockEngine.create {
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
    }

    private val defaultHeaders = headersOf(
        HttpHeaders.ContentType, ContentType.Application.Json.toString()
    )

    private companion object {
        private val USD = makeDollarCurrency()
        private val GHS = makeCediCurrency()
        private val NGN = makeNairaCurrency()

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
                  }, 
                  "${NGN.code}": {
                    "code": "${NGN.code}", 
                    "description": "${NGN.name}"
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
