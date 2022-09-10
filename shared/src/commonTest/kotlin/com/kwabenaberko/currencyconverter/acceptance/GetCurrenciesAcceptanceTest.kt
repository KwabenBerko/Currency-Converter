package com.kwabenaberko.currencyconverter.acceptance

import app.cash.turbine.test
import com.kwabenaberko.currencyconverter.TestContainer
import com.kwabenaberko.sharedtest.builder.CurrencyFactory.makeCediCurrency
import com.kwabenaberko.sharedtest.builder.CurrencyFactory.makeDollarCurrency
import com.kwabenaberko.sharedtest.builder.CurrencyFactory.makeNairaCurrency
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class GetCurrenciesAcceptanceTest {

    private val container = TestContainer()
    private val sut = container.getCurrencies

    @AfterTest
    fun teardown() {
        container.sqlDriver.close()
    }

    @Test
    fun `should return all currencies available to the user in a sorted order`() = runTest {
        with(container.database.dbCurrencyQueries) {
            insert(USD.code, USD.name, USD.symbol)
            insert(GHS.code, GHS.name, GHS.symbol)
            insert(NGN.code, NGN.name, NGN.symbol)
        }

        sut().test {
            assertEquals(SORTED_CURRENCIES, awaitItem())
        }
    }

    private companion object {
        val USD = makeDollarCurrency()
        val GHS = makeCediCurrency()
        val NGN = makeNairaCurrency()
        val SORTED_CURRENCIES = listOf(GHS, NGN, USD)
    }
}
