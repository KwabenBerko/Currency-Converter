package com.kwabenaberko.converter.usecase

import com.kwabenaberko.converter.domain.model.Money
import com.kwabenaberko.converter.domain.usecase.RealConvertMoney
import com.kwabenaberko.convertertest.builder.CurrencyFactory.makeCediCurrency
import com.kwabenaberko.convertertest.builder.CurrencyFactory.makeDollarCurrency
import com.kwabenaberko.convertertest.builder.CurrencyFactory.makeNairaCurrency
import com.kwabenaberko.convertertest.builder.CurrencyFactory.makePoundsCurrency
import com.kwabenaberko.convertertest.testdouble.FakeGetRate
import com.kwabenaberko.convertertest.testdouble.FakeUpdateDefaultCurrencies
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
                row(USD, GHS, 10.015024, 50.0, 500.75),
                row(GHS, NGN, 42.235564, 2000.0, 84471.13),
                row(NGN, GBP, 0.002041, 1.0, 0.0020)
            )
        ) { baseCurrency, targetCurrency, rate, amount, expectedAmount ->

            val updateDefaultCurrencies = FakeUpdateDefaultCurrencies()
            val getRate = FakeGetRate().apply {
                this.result = rate
            }
            val convertMoney = RealConvertMoney(
                getRate = getRate,
                updateDefaultCurrencies = updateDefaultCurrencies
            )
            val expectedMoney = Money(
                currency = targetCurrency, amount = expectedAmount
            )

            val actualMoney = convertMoney(
                money = Money(currency = baseCurrency, amount = amount),
                targetCurrency = targetCurrency
            )

            assertEquals(expectedMoney, actualMoney)
        }
    }

    @Test
    fun `should set default currencies when an amount is converted`() = runTest {
        val updateDefaultCurrencies = FakeUpdateDefaultCurrencies()
        val getRate = FakeGetRate().apply {
            result = 1.0
        }
        val convertMoney = RealConvertMoney(
            getRate = getRate,
            updateDefaultCurrencies = updateDefaultCurrencies
        )

        convertMoney(
            money = Money(currency = GHS, amount = 0.0),
            targetCurrency = NGN
        )

        assertEquals(1, updateDefaultCurrencies.invocations.size)
        assertEquals(Pair(GHS.code, NGN.code), updateDefaultCurrencies.invocations.first())
    }

    private companion object {
        val GHS = makeCediCurrency()
        val NGN = makeNairaCurrency()
        val USD = makeDollarCurrency()
        val GBP = makePoundsCurrency()
    }
}
