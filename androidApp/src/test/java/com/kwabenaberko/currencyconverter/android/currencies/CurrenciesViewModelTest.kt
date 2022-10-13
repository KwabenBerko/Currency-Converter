package com.kwabenaberko.currencyconverter.android.currencies

import app.cash.turbine.test
import com.kwabenaberko.currencyconverter.android.MainDispatcherRule
import com.kwabenaberko.currencyconverter.android.currencies.CurrenciesViewModel.State
import com.kwabenaberko.sharedtest.builder.CurrencyFactory
import com.kwabenaberko.sharedtest.testdouble.FakeGetCurrencies
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class CurrenciesViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getCurrencies = FakeGetCurrencies().apply {
        this.result = flowOf(listOf(GHS, NGN, USD))
    }
    private val sut = CurrenciesViewModel(getCurrencies)

    @Test
    fun `should emit Content state when currencies are loaded`() = runTest {
        val expectedState = State.Content(
            query = "",
            currencies = persistentMapOf(
                'G' to listOf(GHS),
                'N' to listOf(NGN),
                'U' to listOf(USD)
            )
        )

        sut.loadCurrencies()

        sut.state.test {
            assertEquals(State.Idle, awaitItem())
            assertEquals(expectedState, awaitItem())
        }
    }

    @Test
    fun `should emit Content state when currencies are filtered`() = runTest {
        val query = "G"
        val expectedState = State.Content(
            query = query,
            currencies = persistentMapOf(
                'G' to listOf(GHS),
            )
        )
        getCurrencies.result = flowOf(listOf(GHS))

        sut.loadCurrencies(query)

        sut.state.test {
            assertEquals(State.Idle, awaitItem())
            assertEquals(expectedState, awaitItem())
        }
    }

    companion object {
        val GHS = CurrencyFactory.makeCediCurrency()
        val NGN = CurrencyFactory.makeNairaCurrency()
        val USD = CurrencyFactory.makeDollarCurrency()
    }
}