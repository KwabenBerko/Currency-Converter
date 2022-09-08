package com.kwabenaberko.currencyconverter.acceptance

import app.cash.turbine.testIn
import com.kwabenaberko.currencyconverter.TestContainer
import com.kwabenaberko.currencyconverter.builder.CurrencyFactory.makeCediCurrency
import com.kwabenaberko.currencyconverter.builder.CurrencyFactory.makeDollarCurrency
import com.kwabenaberko.currencyconverter.builder.CurrencyFactory.makeNairaCurrency
import com.kwabenaberko.currencyconverter.data.Api
import com.kwabenaberko.currencyconverter.domain.model.SyncStatus
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
        val currenciesObserver = getCurrencies().testIn(this)

        sut.invoke()

        with(statusObserver) {
            assertEquals(SyncStatus.InProgress, awaitItem())
            assertEquals(SyncStatus.Success, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        with(currenciesObserver) {
            assertEquals(emptyList(), awaitItem())
            assertTrue(awaitItem().containsAll(listOf(GHS, USD, NGN)))
            cancelAndIgnoreRemainingEvents()
        }

        forAll(
            table(
                headers("baseCode", "targetCode", "rate"),
                row(USD.code, GHS.code, 9.08),
                row(GHS.code, USD.code, 0.11),
                row(USD.code, NGN.code, 419.22),
                row(NGN.code, USD.code, 0.0024),
                row(GHS.code, NGN.code, 46.11),
                row(NGN.code, GHS.code, 0.022),
            )
        ) { baseCode, targetCode, rate ->
            assertEquals(rate, getRate(baseCode, targetCode))
        }

        assertTrue(hasCompletedInitialSync())
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
                  "${GHS.code}": 9.08, 
                  "${NGN.code}": 419.22
                }
            }
        """.trimIndent()
    }
}
