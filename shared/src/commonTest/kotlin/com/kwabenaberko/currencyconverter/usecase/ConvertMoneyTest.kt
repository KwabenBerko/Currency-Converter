package com.kwabenaberko.currencyconverter.usecase

import com.kwabenaberko.currencyconverter.builder.CurrencyFactory.makeCediCurrency
import com.kwabenaberko.currencyconverter.builder.CurrencyFactory.makeDollarCurrency
import com.kwabenaberko.currencyconverter.builder.CurrencyFactory.makeNairaCurrency
import com.kwabenaberko.currencyconverter.domain.model.Money
import com.kwabenaberko.currencyconverter.domain.usecase.convertMoney
import com.kwabenaberko.currencyconverter.testdouble.GetRateStub
import com.kwabenaberko.currencyconverter.testdouble.SetDefaultCurrenciesSpy
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class ConvertMoneyTest {

    @Test
    fun `should convert amount from base currency to target currency`() = runTest {
        forAll(
            table(
                headers("baseCurrency", "targetCurrency", "rate", "amount", "expectedAmount"),
                row(USD, GHS, 7.775, 50.0, 388.75),
                row(GHS, NGN, 53.347445, 2000.0, 106694.89),
            )
        ) { baseCurrency, targetCurrency, rate, amount, expectedAmount ->

            val setDefaultCurrencies = SetDefaultCurrenciesSpy()
            val getRate = GetRateStub().apply {
                this.result = rate
            }
            val expectedMoney = Money(
                currency = targetCurrency, amount = expectedAmount
            )

            val actualMoney = convertMoney(
                getRate = getRate,
                setDefaultCurrencies = setDefaultCurrencies,
                money = Money(currency = baseCurrency, amount = amount),
                targetCurrency = targetCurrency
            )

            assertEquals(expectedMoney, actualMoney)
        }
    }

    @Test
    fun `should set default currencies when an amount is converted`() = runTest {
        val setDefaultCurrencies = SetDefaultCurrenciesSpy()
        val getRate = GetRateStub().apply {
            result = 1.0
        }

        convertMoney(
            getRate = getRate,
            setDefaultCurrencies = setDefaultCurrencies,
            money = Money(currency = GHS, amount = 0.0),
            targetCurrency = NGN
        )

        assertEquals(1, setDefaultCurrencies.invocations.size)
        assertEquals(Pair(GHS.code, NGN.code), setDefaultCurrencies.invocations.first())
    }

    private companion object {
        val GHS = makeCediCurrency()
        val NGN = makeNairaCurrency()
        val USD = makeDollarCurrency()
    }
}
