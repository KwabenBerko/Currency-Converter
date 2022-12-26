package com.kwabenaberko.converter.acceptance

import app.cash.turbine.test
import com.kwabenaberko.converter.TestContainer
import com.kwabenaberko.converter.domain.model.Money
import com.kwabenaberko.convertertest.builder.CurrencyFactory.makeCediCurrency
import com.kwabenaberko.convertertest.builder.CurrencyFactory.makeCurrency
import com.kwabenaberko.convertertest.builder.CurrencyFactory.makeDollarCurrency
import com.kwabenaberko.convertertest.builder.CurrencyFactory.makeEuroCurrency
import com.kwabenaberko.convertertest.builder.CurrencyFactory.makeNairaCurrency
import com.kwabenaberko.convertertest.builder.CurrencyFactory.makePoundsCurrency
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class ConvertMoneyAcceptanceTest {

    private val container = TestContainer()
    private val sut = container.convertMoney

    @BeforeTest
    fun setup() {
        with(container.database.dbCurrencyQueries) {
            listOf(USD, GHS, NGN, GBP, EUR).forEach { currency ->
                insert(currency.code, currency.name, currency.symbol)
            }
        }
        with(container.database.dbExchangeRateQueries) {
            insert(USD.code, GHS.code, 10.015024)
            insert(GHS.code, NGN.code, 42.235564)
            insert(NGN.code, GBP.code, 0.002041)
            insert(EUR.code, USD.code, 1.007097)
        }
    }

    @AfterTest
    fun teardown() {
        container.sqlDriver.close()
    }

    @Test
    fun `should convert amounts from base to target currencies`() = runTest {
        forAll(
            table(
                headers("baseCode", "targetCode", "amount", "expectedAmount"),
                row(USD.code, GHS.code, 50.0, 500.75),
                row(GHS.code, NGN.code, 2000.0, 84471.13),
                row(NGN.code, GBP.code, 1.0, 0.0020),
                row(EUR.code, USD.code, 900.0, 906.39),
            )
        ) { baseCode: String, targetCode: String, amount: Double, expectedAmount: Double ->
            val expectedMoney = Money(
                currency = makeCurrency(targetCode),
                amount = expectedAmount
            )

            sut(
                Money(currency = makeCurrency(baseCode), amount = amount),
                makeCurrency(targetCode)
            ).test {
                assertEquals(expectedMoney, awaitItem())
            }
        }
    }

    private companion object {
        val USD = makeDollarCurrency()
        val GHS = makeCediCurrency()
        val NGN = makeNairaCurrency()
        val GBP = makePoundsCurrency()
        val EUR = makeEuroCurrency()
    }
}
