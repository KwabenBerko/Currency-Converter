package com.kwabenaberko.converter.acceptance

import com.kwabenaberko.converter.TestContainer
import com.kwabenaberko.converter.domain.model.Currency
import com.kwabenaberko.converter.domain.model.DefaultCurrencies
import com.kwabenaberko.converter.domain.model.Money
import com.kwabenaberko.convertertest.builder.CurrencyFactory.makeCediCurrency
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
class GetDefaultCurrenciesAcceptanceTest {

    private val container = TestContainer()
    private val convertMoney = container.convertMoney
    private val sut = container.getDefaultCurrencies

    @BeforeTest
    fun setup() {
        with(container.database.dbCurrencyQueries) {
            CURRENCIES.forEach { currency ->
                insert(code = currency.code, name = currency.name, symbol = currency.symbol)
            }
        }
        with(container.database.dbExchangeRateQueries) {
            insert(baseCode = USD.code, targetCode = GHS.code, rate = 0.0)
            insert(baseCode = GHS.code, targetCode = NGN.code, rate = 0.0)
            insert(baseCode = NGN.code, targetCode = GBP.code, rate = 0.0)
            insert(baseCode = EUR.code, targetCode = USD.code, rate = 0.0)
        }
    }

    @AfterTest
    fun teardown() {
        container.sqlDriver.close()
    }

    @Test
    fun `should return USD and GHS as the base and target currency respectively if no default currencies exist`() =
        runTest {
            assertEquals(DefaultCurrencies(USD, GHS), sut())
        }

    @Test
    fun `should keep track of base and target currencies during conversions`() = runTest {
        forAll(
            table(
                headers("baseCurrency", "targetCurrency"),
                row(USD, GHS),
                row(GHS, NGN),
                row(NGN, GBP),
                row(EUR, USD),
            )
        ) { baseCurrency: Currency, targetCurrency: Currency ->

            convertMoney(
                Money(currency = baseCurrency, amount = 0.0),
                targetCurrency
            )

            assertEquals(DefaultCurrencies(baseCurrency, targetCurrency), sut())
        }
    }

    companion object {
        val USD = makeDollarCurrency()
        val GHS = makeCediCurrency()
        val NGN = makeNairaCurrency()
        val GBP = makePoundsCurrency()
        val EUR = makeEuroCurrency()
        val CURRENCIES = listOf(USD, GHS, NGN, GBP, EUR)
    }
}