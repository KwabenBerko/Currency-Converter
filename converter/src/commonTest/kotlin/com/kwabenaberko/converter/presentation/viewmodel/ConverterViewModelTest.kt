package com.kwabenaberko.converter.presentation.viewmodel

import app.cash.turbine.test
import com.kwabenaberko.converter.domain.model.DefaultCurrencies
import com.kwabenaberko.converter.domain.model.Money
import com.kwabenaberko.converter.presentation.model.ConversionMode
import com.kwabenaberko.converter.presentation.viewmodel.ConverterViewModel.MoneyViewItem
import com.kwabenaberko.converter.presentation.viewmodel.ConverterViewModel.State
import com.kwabenaberko.converter.testdouble.FakeConvertMoney
import com.kwabenaberko.converter.testdouble.FakeGetDefaultCurrencies
import com.kwabenaberko.converter.testdouble.FakeHasCompletedInitialSync
import com.kwabenaberko.convertertest.builder.CurrencyFactory.makeCediCurrency
import com.kwabenaberko.convertertest.builder.CurrencyFactory.makeDollarCurrency
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

@OptIn(ExperimentalCoroutinesApi::class)
class ConverterViewModelTest {
    private val hasCompletedInitialSync = FakeHasCompletedInitialSync().apply {
        this.result = flowOf(true)
    }
    private val getDefaultCurrencies = FakeGetDefaultCurrencies().apply {
        this.result = flowOf(DefaultCurrencies(base = GHS, target = GHS))
    }
    private val convertMoney = FakeConvertMoney().apply {
        this.result = flowOf(Money(currency = GHS, amount = 1.0))
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
    fun `should emit RequiresSync state if initial sync has not been completed`() = runTest {
        hasCompletedInitialSync.result = flowOf(false)
        val sut = createViewModel()

        sut.state.test {
            assertEquals(State.Idle, awaitItem())
            assertEquals(State.RequiresSync, awaitItem())
        }
    }

    @Test
    fun `should emit Content state when converter loads`() = runTest {
        val firstMoney = Money(GHS, 1.0)
        val secondMoney = Money(GHS, 1.0)
        val expectedState = State.Content(
            firstMoneyItem = MoneyViewItem(money = firstMoney, formattedAmount = "1"),
            secondMoneyItem = MoneyViewItem(money = secondMoney, formattedAmount = "1"),
            conversionMode = ConversionMode.FIRST_TO_SECOND
        )
        val sut = createViewModel()

        sut.state.test {
            assertEquals(State.Idle, awaitItem())
            assertEquals(expectedState, awaitItem())
        }
    }

    @Test
    fun `should emit Content State when first currency changes`() = runTest {
        val firstMoney = Money(GHS, 1.0)
        val secondMoney = Money(GHS, 1.0)
        val initialExpectedState = State.Content(
            firstMoneyItem = MoneyViewItem(money = firstMoney, formattedAmount = "1"),
            secondMoneyItem = MoneyViewItem(money = secondMoney, formattedAmount = "1"),
            conversionMode = ConversionMode.FIRST_TO_SECOND
        )
        val nextExpectedState = State.Content(
            firstMoneyItem = MoneyViewItem(
                money = firstMoney.copy(currency = USD), formattedAmount = "1"
            ),
            secondMoneyItem = MoneyViewItem(
                money = secondMoney.copy(amount = 10.0), formattedAmount = "10"
            ),
            conversionMode = ConversionMode.FIRST_TO_SECOND
        )
        val sut = createViewModel()

        sut.state.test {
            assertEquals(State.Idle, awaitItem())
            assertEquals(initialExpectedState, awaitItem())

            convertMoney.result = flowOf(Money(currency = GHS, amount = 10.0))
            sut.convertFirstMoney(USD)

            assertEquals(nextExpectedState, awaitItem())
        }
    }

    @Test
    fun `should emit Content State when first amount changes`() = runTest {
        val firstMoney = Money(GHS, 1.0)
        val secondMoney = Money(GHS, 1.0)
        val initialExpectedState = State.Content(
            firstMoneyItem = MoneyViewItem(money = firstMoney, formattedAmount = "1"),
            secondMoneyItem = MoneyViewItem(money = secondMoney, formattedAmount = "1"),
            conversionMode = ConversionMode.FIRST_TO_SECOND
        )
        val nextExpectedState = State.Content(
            firstMoneyItem = MoneyViewItem(
                money = firstMoney.copy(amount = 1000.0), formattedAmount = "1K"
            ),
            secondMoneyItem = MoneyViewItem(
                money = secondMoney.copy(amount = 1000.0), formattedAmount = "1K"
            ),
            conversionMode = ConversionMode.FIRST_TO_SECOND
        )
        val sut = createViewModel()

        sut.state.test {
            assertEquals(State.Idle, awaitItem())
            assertEquals(initialExpectedState, awaitItem())

            convertMoney.result = flowOf(Money(currency = GHS, amount = 1000.0))
            sut.convertFirstMoney(1000.0)

            assertEquals(nextExpectedState, awaitItem())
        }
    }

    @Test
    fun `should emit Content State when second currency changes`() = runTest {
        val firstMoney = Money(GHS, 1.0)
        val secondMoney = Money(GHS, 1.0)
        val initialExpectedState = State.Content(
            firstMoneyItem = MoneyViewItem(money = firstMoney, formattedAmount = "1"),
            secondMoneyItem = MoneyViewItem(money = secondMoney, formattedAmount = "1"),
            conversionMode = ConversionMode.FIRST_TO_SECOND
        )
        val nextExpectedState = State.Content(
            firstMoneyItem = MoneyViewItem(
                money = firstMoney.copy(amount = 10.0), formattedAmount = "10"
            ),
            secondMoneyItem = MoneyViewItem(
                money = secondMoney.copy(currency = USD), formattedAmount = "1"
            ),
            conversionMode = ConversionMode.SECOND_TO_FIRST
        )
        val sut = createViewModel()

        sut.state.test {
            assertEquals(State.Idle, awaitItem())
            assertEquals(initialExpectedState, awaitItem())

            convertMoney.result = flowOf(Money(currency = GHS, amount = 10.0))
            sut.convertSecondMoney(USD)

            assertEquals(nextExpectedState, awaitItem())
        }
    }

    @Test
    fun `should emit Content State when second amount changes`() = runTest {
        val firstMoney = Money(GHS, 1.0)
        val secondMoney = Money(GHS, 1.0)
        val initialExpectedState = State.Content(
            firstMoneyItem = MoneyViewItem(money = firstMoney, formattedAmount = "1"),
            secondMoneyItem = MoneyViewItem(money = secondMoney, formattedAmount = "1"),
            conversionMode = ConversionMode.FIRST_TO_SECOND
        )
        val nextExpectedState = State.Content(
            firstMoneyItem = MoneyViewItem(
                money = firstMoney.copy(amount = 1000.0), formattedAmount = "1K"
            ),
            secondMoneyItem = MoneyViewItem(
                money = secondMoney.copy(amount = 1000.0), formattedAmount = "1K"
            ),
            conversionMode = ConversionMode.SECOND_TO_FIRST
        )
        val sut = createViewModel()

        sut.state.test {
            assertEquals(State.Idle, awaitItem())
            assertEquals(initialExpectedState, awaitItem())

            convertMoney.result = flowOf(Money(currency = GHS, amount = 1000.0))
            sut.convertSecondMoney(1000.0)

            assertEquals(nextExpectedState, awaitItem())
        }
    }

    private fun createViewModel(): ConverterViewModel {
        return ConverterViewModel(
            hasCompletedInitialSync = hasCompletedInitialSync,
            getDefaultCurrencies = getDefaultCurrencies,
            convertMoney = convertMoney
        )
    }

    companion object {
        val GHS = makeCediCurrency()
        val USD = makeDollarCurrency()
    }
}
