package com.kwabenaberko.converter.acceptance

import app.cash.turbine.test
import com.kwabenaberko.converter.TestContainer
import com.kwabenaberko.converter.builder.CurrencyFactory.makeCediCurrency
import com.kwabenaberko.converter.builder.CurrencyFactory.makeDollarCurrency
import com.kwabenaberko.converter.builder.CurrencyFactory.makeEuroCurrency
import com.kwabenaberko.converter.builder.CurrencyFactory.makeNairaCurrency
import com.kwabenaberko.converter.builder.CurrencyFactory.makePoundsCurrency
import com.kwabenaberko.converter.database.DbCurrency
import com.kwabenaberko.converter.database.DbExchangeRate
import com.kwabenaberko.converter.domain.model.Currency
import com.kwabenaberko.converter.domain.model.DefaultCurrencies
import com.kwabenaberko.converter.domain.model.Money
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(FlowPreview::class)
@ExperimentalCoroutinesApi
class GetDefaultCurrenciesAcceptanceTest {

    private val container = TestContainer()
    private val convertMoney = container.convertMoney
    private val sut = container.getDefaultCurrencies

    @BeforeTest
    fun setup() {
        with(container.database.dbCurrencyQueries) {
            CURRENCIES.forEach { currency ->
                insert(DbCurrency(currency.code, currency.name, currency.symbol))
            }
        }
        with(container.database.dbExchangeRateQueries) {
            insert(DbExchangeRate(USD.code, GHS.code, 0.0))
            insert(DbExchangeRate(GHS.code, NGN.code, 0.0))
            insert(DbExchangeRate(NGN.code, GBP.code, 0.0))
            insert(DbExchangeRate(EUR.code, USD.code, 0.0))
        }
    }

    @AfterTest
    fun teardown() {
        container.sqlDriver.close()
    }

    @Test
    fun `should return USD and GHS as the base and target currency respectively if no default currencies exist`() =
        runTest {
            sut().test {
                assertEquals(DefaultCurrencies(USD, GHS), awaitItem())
            }
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

            val result = convertMoney(Money(currency = baseCurrency, amount = 0.0), targetCurrency)
                .flatMapConcat { sut() }

            result.test {
                assertEquals(DefaultCurrencies(baseCurrency, targetCurrency), awaitItem())
            }
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
