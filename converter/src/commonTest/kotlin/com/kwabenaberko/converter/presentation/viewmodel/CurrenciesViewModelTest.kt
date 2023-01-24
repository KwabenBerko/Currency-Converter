package com.kwabenaberko.converter.presentation.viewmodel

import app.cash.turbine.test
import com.kwabenaberko.converter.presentation.viewmodel.CurrenciesViewModel.State
import com.kwabenaberko.converter.testdouble.FakeGetCurrencies
import com.kwabenaberko.convertertest.builder.CurrencyFactory
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class CurrenciesViewModelTest {

    private val getCurrencies = FakeGetCurrencies().apply {
        this.result = flowOf(listOf(GHS, NGN, USD))
    }

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should emit Content state when currencies are loaded`() = runTest {
        val expectedState = State.Content(
            selectedCurrency = GHS,
            currencies = persistentMapOf(
                "G" to listOf(GHS),
                "N" to listOf(NGN),
                "U" to listOf(USD)
            )
        )
        val sut = createViewModel(selectedCurrencyCode = GHS.code)

        sut.state.test {
            assertEquals(State.Idle, awaitItem())
            assertEquals(expectedState, awaitItem())
        }
    }

    @Test
    fun `should emit Content state when currencies are filtered`() = runTest {
        val query = "G"
        val initialExpectedState = State.Content(
            selectedCurrency = GHS,
            currencies = persistentMapOf(
                "G" to listOf(GHS),
                "N" to listOf(NGN),
                "U" to listOf(USD)
            )
        )
        val nextExpectedState = initialExpectedState.copy(
            currencies = persistentMapOf(
                "G" to listOf(GHS),
            )
        )
        val sut = createViewModel(selectedCurrencyCode = GHS.code)

        sut.state.test {
            assertEquals(State.Idle, awaitItem())
            assertEquals(initialExpectedState, awaitItem())

            getCurrencies.result = flowOf(listOf(GHS))
            sut.filterCurrencies(query)

            assertEquals(nextExpectedState, awaitItem())
        }
    }

    private fun createViewModel(selectedCurrencyCode: String): CurrenciesViewModel {
        return CurrenciesViewModel(
            selectedCurrencyCode = selectedCurrencyCode,
            getCurrencies = getCurrencies
        )
    }

    companion object {
        val GHS = CurrencyFactory.makeCediCurrency()
        val NGN = CurrencyFactory.makeNairaCurrency()
        val USD = CurrencyFactory.makeDollarCurrency()
    }
}
