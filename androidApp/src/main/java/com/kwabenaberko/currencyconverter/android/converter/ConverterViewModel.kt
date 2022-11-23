package com.kwabenaberko.currencyconverter.android.converter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kwabenaberko.converter.domain.model.Money
import com.kwabenaberko.converter.domain.usecase.ConvertMoney
import com.kwabenaberko.converter.domain.usecase.GetDefaultCurrencies
import com.kwabenaberko.converter.domain.usecase.HasCompletedInitialSync
import com.kwabenaberko.converter.presentation.CompactNumberFormatter
import com.kwabenaberko.currencyconverter.android.converter.model.ConversionMode
import com.kwabenaberko.currencyconverter.android.runIf
import kotlinx.coroutines.launch

class ConverterViewModel(
    private val hasCompletedInitialSync: HasCompletedInitialSync,
    private val getDefaultCurrencies: GetDefaultCurrencies,
    private val convertMoney: ConvertMoney
) : com.kwabenaberko.currencyconverter.android.BaseViewModel<ConverterViewModel.State>(State.Idle) {

    private val formatter = CompactNumberFormatter()

    init {
        viewModelScope.launch {
            if (!hasCompletedInitialSync()) {
                setState(State.RequiresSync)
            } else {
                loadConverter()
            }
        }
    }

    private suspend fun loadConverter() {
        val (base, target) = getDefaultCurrencies()
        val firstMoney = Money(currency = base, amount = 1.0)
        val secondMoney = convertMoney(firstMoney, target)
        val firstMoneyItem = mapMoneyToViewItem(firstMoney)
        val secondMoneyItem = mapMoneyToViewItem(secondMoney)

        val newState = State.Content(
            firstMoneyItem = firstMoneyItem,
            secondMoneyItem = secondMoneyItem,
            conversionMode = ConversionMode.FIRST_MONEY_TO_SECOND_MONEY
        )

        setState(newState)
    }

    fun convertFirstMoney(money: Money) = getState().runIf<State.Content> { contentState ->
        viewModelScope.launch {
            val secondMoneyItem = contentState.secondMoneyItem
            val targetCurrency = secondMoneyItem.money.currency
            val secondMoney = convertMoney(money, targetCurrency)

            val newState = contentState.copy(
                firstMoneyItem = mapMoneyToViewItem(money),
                secondMoneyItem = mapMoneyToViewItem(secondMoney),
                conversionMode = ConversionMode.FIRST_MONEY_TO_SECOND_MONEY
            )

            setState(newState)
        }
    }

    fun convertSecondMoney(money: Money) = getState().runIf<State.Content> { contentState ->
        viewModelScope.launch {
            val firstMoneyItem = contentState.firstMoneyItem
            val targetCurrency = firstMoneyItem.money.currency
            val firstMoney = convertMoney(money, targetCurrency)

            val newState = contentState.copy(
                firstMoneyItem = mapMoneyToViewItem(firstMoney),
                secondMoneyItem = mapMoneyToViewItem(money),
                conversionMode = ConversionMode.SECOND_MONEY_TO_FIRST_MONEY
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
        object RequiresSync : State()
        data class Content(
            val firstMoneyItem: MoneyViewItem,
            val secondMoneyItem: MoneyViewItem,
            val conversionMode: ConversionMode
        ) : State()
    }

    class Factory(
        private val hasCompletedInitialSync: HasCompletedInitialSync,
        private val getDefaultCurrencies: GetDefaultCurrencies,
        private val convertMoney: ConvertMoney
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ConverterViewModel(
                hasCompletedInitialSync = hasCompletedInitialSync,
                getDefaultCurrencies = getDefaultCurrencies,
                convertMoney = convertMoney
            ) as T
        }
    }
}
