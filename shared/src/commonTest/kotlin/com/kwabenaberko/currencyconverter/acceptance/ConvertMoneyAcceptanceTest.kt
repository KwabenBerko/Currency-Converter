package com.kwabenaberko.currencyconverter.acceptance

import com.kwabenaberko.currencyconverter.TestContainer
import com.kwabenaberko.currencyconverter.builder.CurrencyFactory.makeCediCurrency
import com.kwabenaberko.currencyconverter.builder.CurrencyFactory.makeCurrency
import com.kwabenaberko.currencyconverter.builder.CurrencyFactory.makeDollarCurrency
import com.kwabenaberko.currencyconverter.builder.CurrencyFactory.makeEuroCurrency
import com.kwabenaberko.currencyconverter.builder.CurrencyFactory.makeNairaCurrency
import com.kwabenaberko.currencyconverter.builder.CurrencyFactory.makePoundsCurrency
import com.kwabenaberko.currencyconverter.domain.model.Money
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
        with(container.database.dbExchangeRateQueries){
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
                row(NGN.code, GBP.code, 100000.0, 204.1),
                row(EUR.code, USD.code, 900.0, 906.39),
            )
        ) { baseCode: String, targetCode: String, amount: Double, expectedAmount: Double ->
            val expectedMoney = Money(
                currency = makeCurrency(targetCode), amount = expectedAmount
            )

            val actualMoney = sut(
                Money(currency = makeCurrency(baseCode), amount = amount),
                makeCurrency(targetCode)
            )

            assertEquals(expectedMoney, actualMoney)
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
