package com.kwabenaberko.converter.acceptance

import app.cash.turbine.test
import com.kwabenaberko.converter.TestContainer
import com.kwabenaberko.converter.database.DbCurrency
import com.kwabenaberko.converter.builder.CurrencyFactory.makeCediCurrency
import com.kwabenaberko.converter.builder.CurrencyFactory.makeDollarCurrency
import com.kwabenaberko.converter.builder.CurrencyFactory.makeNairaCurrency
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
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
            insert(DbCurrency(USD.code, USD.name, USD.symbol))
            insert(DbCurrency(GHS.code, GHS.name, GHS.symbol))
            insert(DbCurrency(NGN.code, NGN.name, NGN.symbol))
        }

        sut(null).test {
            assertEquals(SORTED_CURRENCIES, awaitItem())
        }
    }

    @Test
    fun `should filter currencies by name in a sorted order`() = runTest {
        with(container.database.dbCurrencyQueries) {
            insert(DbCurrency(USD.code, USD.name, USD.symbol))
            insert(DbCurrency(GHS.code, GHS.name, GHS.symbol))
            insert(DbCurrency(NGN.code, NGN.name, NGN.symbol))
        }

        forAll(
            table(
                headers("filter", "filteredCurrencies"),
                row("e", listOf(GHS, NGN, USD)),
                row("g", listOf(GHS, NGN)),
                row(GHS.code, listOf(GHS)),
                row("Ghana", listOf(GHS)),
                row("naira", listOf(NGN)),
                row(USD.symbol, listOf(USD))
            )
        ) { filter, filteredCurrencies ->

            sut(filter).test {
                assertEquals(filteredCurrencies, awaitItem())
            }
        }
    }

    private companion object {
        val USD = makeDollarCurrency()
        val GHS = makeCediCurrency()
        val NGN = makeNairaCurrency()
        val SORTED_CURRENCIES = listOf(GHS, NGN, USD)
    }
}
