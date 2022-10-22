package com.kwabenaberko.currencyconverter.android.converter

import app.cash.turbine.test
import com.kwabenaberko.currencyconverter.android.MainDispatcherRule
import com.kwabenaberko.currencyconverter.android.converter.ConverterViewModel.MoneyViewItem
import com.kwabenaberko.currencyconverter.android.converter.ConverterViewModel.State
import com.kwabenaberko.currencyconverter.android.converter.model.ConversionMode
import com.kwabenaberko.currencyconverter.domain.model.DefaultCurrencies
import com.kwabenaberko.currencyconverter.domain.model.Money
import com.kwabenaberko.sharedtest.builder.CurrencyFactory.makeCediCurrency
import com.kwabenaberko.sharedtest.builder.CurrencyFactory.makeDollarCurrency
import com.kwabenaberko.sharedtest.testdouble.FakeConvertMoney
import com.kwabenaberko.sharedtest.testdouble.FakeGetDefaultCurrencies
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ConverterViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getDefaultCurrencies = FakeGetDefaultCurrencies().apply {
        this.result = DefaultCurrencies(base = GHS, target = GHS)
    }
    private val convertMoney = FakeConvertMoney().apply {
        this.result = Money(currency = GHS, amount = 1.0)
    }

    @Test
    fun `should emit Content state when converter loads`() = runTest {
        val firstMoney = Money(GHS, 1.0)
        val secondMoney = Money(GHS, 1.0)
        val expectedState = State.Content(
            firstMoneyItem = MoneyViewItem(money = firstMoney, formattedAmount = "1.0"),
            secondMoneyItem = MoneyViewItem(money = secondMoney, formattedAmount = "1.0"),
            conversionMode = ConversionMode.FIRST_MONEY_TO_SECOND_MONEY
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
            firstMoneyItem = MoneyViewItem(money = firstMoney, formattedAmount = "1.0"),
            secondMoneyItem = MoneyViewItem(money = secondMoney, formattedAmount = "1.0"),
            conversionMode = ConversionMode.FIRST_MONEY_TO_SECOND_MONEY
        )
        val nextExpectedState = State.Content(
            firstMoneyItem = MoneyViewItem(
                money = firstMoney.copy(currency = USD), formattedAmount = "1.0"
            ),
            secondMoneyItem = MoneyViewItem(
                money = secondMoney.copy(amount = 10.0), formattedAmount = "10.0"
            ),
            conversionMode = ConversionMode.FIRST_MONEY_TO_SECOND_MONEY
        )
        val sut = createViewModel()

        sut.state.test {
            assertEquals(State.Idle, awaitItem())
            assertEquals(initialExpectedState, awaitItem())

            convertMoney.result = Money(currency = GHS, amount = 10.0)
            sut.convertFirstMoney(firstMoney.copy(currency = USD))

            assertEquals(nextExpectedState, awaitItem())
        }
    }

    @Test
    fun `should emit Content State when first amount changes`() = runTest {
        val firstMoney = Money(GHS, 1.0)
        val secondMoney = Money(GHS, 1.0)
        val initialExpectedState = State.Content(
            firstMoneyItem = MoneyViewItem(money = firstMoney, formattedAmount = "1.0"),
            secondMoneyItem = MoneyViewItem(money = secondMoney, formattedAmount = "1.0"),
            conversionMode = ConversionMode.FIRST_MONEY_TO_SECOND_MONEY
        )
        val nextExpectedState = State.Content(
            firstMoneyItem = MoneyViewItem(
                money = firstMoney.copy(amount = 1000.0), formattedAmount = "1K"
            ),
            secondMoneyItem = MoneyViewItem(
                money = secondMoney.copy(amount = 1000.0), formattedAmount = "1K"
            ),
            conversionMode = ConversionMode.FIRST_MONEY_TO_SECOND_MONEY
        )
        val sut = createViewModel()

        sut.state.test {
            assertEquals(State.Idle, awaitItem())
            assertEquals(initialExpectedState, awaitItem())

            convertMoney.result = Money(currency = GHS, amount = 1000.0)
            sut.convertFirstMoney(firstMoney.copy(amount = 1000.0))

            assertEquals(nextExpectedState, awaitItem())
        }
    }

    @Test
    fun `should emit Content State when second currency changes`() = runTest {
        val firstMoney = Money(GHS, 1.0)
        val secondMoney = Money(GHS, 1.0)
        val initialExpectedState = State.Content(
            firstMoneyItem = MoneyViewItem(money = firstMoney, formattedAmount = "1.0"),
            secondMoneyItem = MoneyViewItem(money = secondMoney, formattedAmount = "1.0"),
            conversionMode = ConversionMode.FIRST_MONEY_TO_SECOND_MONEY
        )
        val nextExpectedState = State.Content(
            firstMoneyItem = MoneyViewItem(
                money = firstMoney.copy(amount = 10.0), formattedAmount = "10.0"
            ),
            secondMoneyItem = MoneyViewItem(
                money = secondMoney.copy(currency = USD), formattedAmount = "1.0"
            ),
            conversionMode = ConversionMode.SECOND_MONEY_TO_FIRST_MONEY
        )
        val sut = createViewModel()

        sut.state.test {
            assertEquals(State.Idle, awaitItem())
            assertEquals(initialExpectedState, awaitItem())

            convertMoney.result = Money(currency = GHS, amount = 10.0)
            sut.convertSecondMoney(secondMoney.copy(currency = USD))

            assertEquals(nextExpectedState, awaitItem())
        }
    }

    @Test
    fun `should emit Content State when second amount changes`() = runTest {
        val firstMoney = Money(GHS, 1.0)
        val secondMoney = Money(GHS, 1.0)
        val initialExpectedState = State.Content(
            firstMoneyItem = MoneyViewItem(money = firstMoney, formattedAmount = "1.0"),
            secondMoneyItem = MoneyViewItem(money = secondMoney, formattedAmount = "1.0"),
            conversionMode = ConversionMode.FIRST_MONEY_TO_SECOND_MONEY
        )
        val nextExpectedState = State.Content(
            firstMoneyItem = MoneyViewItem(
                money = firstMoney.copy(amount = 1000.0), formattedAmount = "1K"
            ),
            secondMoneyItem = MoneyViewItem(
                money = secondMoney.copy(amount = 1000.0), formattedAmount = "1K"
            ),
            conversionMode = ConversionMode.SECOND_MONEY_TO_FIRST_MONEY
        )
        val sut = createViewModel()

        sut.state.test {
            assertEquals(State.Idle, awaitItem())
            assertEquals(initialExpectedState, awaitItem())

            convertMoney.result = Money(currency = GHS, amount = 1000.0)
            sut.convertSecondMoney(secondMoney.copy(amount = 1000.0))

            assertEquals(nextExpectedState, awaitItem())
        }
    }

    private fun createViewModel(): ConverterViewModel {
        return ConverterViewModel(
            getDefaultCurrencies = getDefaultCurrencies,
            convertMoney = convertMoney
        )
    }

    companion object {
        val GHS = makeCediCurrency()
        val USD = makeDollarCurrency()
    }
}
