package com.kwabenaberko.converter.android.currencies

import app.cash.turbine.test
import com.kwabenaberko.converter.android.MainDispatcherRule
import com.kwabenaberko.convertertest.builder.CurrencyFactory
import com.kwabenaberko.convertertest.testdouble.FakeGetCurrencies
import com.kwabenaberko.currencyconverter.android.currencies.CurrenciesViewModel
import com.kwabenaberko.currencyconverter.android.currencies.CurrenciesViewModel.State
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

    @Test
    fun `should emit Content state when currencies are loaded`() = runTest {
        val expectedState = State.Content(
            currencies = persistentMapOf(
                'G' to listOf(GHS),
                'N' to listOf(NGN),
                'U' to listOf(USD)
            )
        )
        val sut = createViewModel()

        sut.state.test {
            assertEquals(State.Idle, awaitItem())
            assertEquals(expectedState, awaitItem())
        }
    }

    @Test
    fun `should emit Content state when currencies are filtered`() = runTest {
        val query = "G"
        val initialExpectedState = State.Content(
            currencies = persistentMapOf(
                'G' to listOf(GHS),
                'N' to listOf(NGN),
                'U' to listOf(USD)
            )
        )
        val nextExpectedState = State.Content(
            currencies = persistentMapOf(
                'G' to listOf(GHS),
            )
        )
        val sut = createViewModel()

        sut.state.test {
            assertEquals(State.Idle, awaitItem())
            assertEquals(initialExpectedState, awaitItem())

            getCurrencies.result = flowOf(listOf(GHS))
            sut.filterCurrencies(query)

            assertEquals(nextExpectedState, awaitItem())
        }
    }

    private fun createViewModel(): CurrenciesViewModel {
        return CurrenciesViewModel(getCurrencies)
    }

    companion object {
        val GHS = CurrencyFactory.makeCediCurrency()
        val NGN = CurrencyFactory.makeNairaCurrency()
        val USD = CurrencyFactory.makeDollarCurrency()
    }
}
