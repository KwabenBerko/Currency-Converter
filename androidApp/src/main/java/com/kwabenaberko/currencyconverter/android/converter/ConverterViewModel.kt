package com.kwabenaberko.currencyconverter.android.converter

import androidx.lifecycle.viewModelScope
import com.kwabenaberko.currencyconverter.android.BaseViewModel
import com.kwabenaberko.currencyconverter.domain.model.Money
import com.kwabenaberko.currencyconverter.domain.usecase.ConvertMoney
import com.kwabenaberko.currencyconverter.domain.usecase.GetDefaultCurrencies
import com.kwabenaberko.currencyconverter.presentation.CompactNumberFormatter
import kotlinx.coroutines.launch

class ConverterViewModel(
    private val getDefaultCurrencies: GetDefaultCurrencies,
    private val convertMoney: ConvertMoney
) : BaseViewModel<ConverterViewModel.State>(State.Idle) {

    private val formatter = CompactNumberFormatter()

    fun loadConverter() {
        viewModelScope.launch {
            val (base, target) = getDefaultCurrencies()
            val firstMoney = Money(currency = base, amount = 1.0)
            val secondMoney = convertMoney(firstMoney, target)
            val firstMoneyItem = mapMoneyToViewItem(firstMoney)
            val secondMoneyItem = mapMoneyToViewItem(secondMoney)

            val newState = State.Content(
                firstMoneyItem = firstMoneyItem,
                secondMoneyItem = secondMoneyItem,
                isReverse = false
            )

            setState(newState)
        }
    }

    fun convertFirstMoney(money: Money) = runIf<State.Content> { contentState ->
        viewModelScope.launch {
            val secondMoneyItem = contentState.secondMoneyItem
            val targetCurrency = secondMoneyItem.money.currency
            val secondMoney = convertMoney(money, targetCurrency)

            val newState = contentState.copy(
                firstMoneyItem = mapMoneyToViewItem(money),
                secondMoneyItem = mapMoneyToViewItem(secondMoney)
            )

            setState(newState)
        }
    }

    fun convertSecondMoney(money: Money) = runIf<State.Content> { contentState ->
        viewModelScope.launch {
            val firstMoneyItem = contentState.firstMoneyItem
            val targetCurrency = firstMoneyItem.money.currency
            val firstMoney = convertMoney(money, targetCurrency)

            val newState = contentState.copy(
                firstMoneyItem = mapMoneyToViewItem(firstMoney),
                secondMoneyItem = mapMoneyToViewItem(money),
                isReverse = true
            )

            setState(newState)
        }
    }

    private fun mapMoneyToViewItem(money: Money): MoneyViewItem {
        return MoneyViewItem(
            money = money,
            formattedAmount = formatter.format(money.amount)
        )
    }

    data class MoneyViewItem(
        val money: Money,
        val formattedAmount: String
    )

    sealed class State {
        object Idle : State()
        data class Content(
            val firstMoneyItem: MoneyViewItem,
            val secondMoneyItem: MoneyViewItem,
            val isReverse: Boolean
        ) : State()
    }
}
