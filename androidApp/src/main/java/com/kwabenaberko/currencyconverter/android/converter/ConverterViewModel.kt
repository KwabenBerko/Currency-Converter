package com.kwabenaberko.currencyconverter.android.converter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kwabenaberko.converter.domain.model.Currency
import com.kwabenaberko.converter.domain.model.Money
import com.kwabenaberko.converter.domain.usecase.ConvertMoney
import com.kwabenaberko.converter.domain.usecase.GetDefaultCurrencies
import com.kwabenaberko.converter.domain.usecase.HasCompletedInitialSync
import com.kwabenaberko.converter.presentation.CompactNumberFormatter
import com.kwabenaberko.currencyconverter.android.BaseViewModel
import com.kwabenaberko.currencyconverter.android.converter.ConverterViewModel.State.*
import com.kwabenaberko.currencyconverter.android.converter.model.ConversionMode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ConverterViewModel(
    private val hasCompletedInitialSync: HasCompletedInitialSync,
    private val getDefaultCurrencies: GetDefaultCurrencies,
    private val convertMoney: ConvertMoney
) : BaseViewModel<ConverterViewModel.State>(Idle) {

    private val converterFlow = MutableSharedFlow<Triple<Money, Currency, ConversionMode>>()
    private val formatter = CompactNumberFormatter()

    init {
        hasCompletedInitialSync()
            .take(1)
            .onEach { hasCompleted ->
                when (hasCompleted) {
                    true -> loadConverter()
                    false -> setState(RequiresSync)
                }
            }.launchIn(viewModelScope)
    }

    private fun loadConverter() {
        converterFlow
            .onStart { emit(initial()) }
            .flatMapLatest { data ->
                val (money, target) = data
                convertMoney(money, target).map { convertedMoney ->
                    data to convertedMoney
                }

            }
            .onEach { pair ->
                val (data, convertedMoney) = pair
                val (money, _, conversionMode) = data

                val firstMoney = when (conversionMode) {
                    ConversionMode.FIRST_TO_SECOND -> money
                    ConversionMode.SECOND_TO_FIRST -> convertedMoney
                }
                val secondMoney = when (conversionMode) {
                    ConversionMode.FIRST_TO_SECOND -> convertedMoney
                    ConversionMode.SECOND_TO_FIRST -> money
                }

                val firstMoneyItem = moneyToViewItem(firstMoney)
                val secondMoneyItem = moneyToViewItem(secondMoney)

                val newState = Content(
                    firstMoneyItem = firstMoneyItem,
                    secondMoneyItem = secondMoneyItem,
                    conversionMode = conversionMode
                )

                setState(newState)

            }.launchIn(viewModelScope)
    }

    fun convertFirstMoney(amount: Double) = runIf<Content> { contentState ->
        val (firstMoney, secondMoney) = moniesFrom(contentState)

        convertFirstMoney(
            money = firstMoney.copy(amount = amount),
            targetCurrency = secondMoney.currency
        )
    }

    fun convertFirstMoney(currency: Currency) = runIf<Content> { contentState ->
        val (firstMoney, secondMoney) = moniesFrom(contentState)

        convertFirstMoney(
            money = firstMoney.copy(currency = currency),
            targetCurrency = secondMoney.currency
        )
    }

    fun convertSecondMoney(amount: Double) = runIf<Content> { contentState ->
        val (firstMoney, secondMoney) = moniesFrom(contentState)

        convertSecondMoney(
            money = secondMoney.copy(amount = amount),
            targetCurrency = firstMoney.currency
        )
    }

    fun convertSecondMoney(currency: Currency) = runIf<Content> { contentState ->
        val (firstMoney, secondMoney) = moniesFrom(contentState)

        convertSecondMoney(
            money = secondMoney.copy(currency = currency),
            targetCurrency = firstMoney.currency
        )
    }

    private fun convertFirstMoney(money: Money, targetCurrency: Currency) {
        val data = Triple(money, targetCurrency, ConversionMode.FIRST_TO_SECOND)
        viewModelScope.launch {
            converterFlow.emit(data)
        }
    }

    private fun convertSecondMoney(money: Money, targetCurrency: Currency) {
        val data = Triple(money, targetCurrency, ConversionMode.SECOND_TO_FIRST)
        viewModelScope.launch {
            converterFlow.emit(data)
        }
    }

    private suspend fun initial(): Triple<Money, Currency, ConversionMode> {
        val (base, target) = getDefaultCurrencies().first()
        val money = Money(base, 1.0)
        return Triple(money, target, ConversionMode.FIRST_TO_SECOND)
    }

    private fun moniesFrom(contentState: Content): Pair<Money, Money> {
        val (firstMoneyViewItem, secondMoneyViewItem) = contentState
        return Pair(firstMoneyViewItem.money, secondMoneyViewItem.money)
    }

    private fun moneyToViewItem(money: Money): MoneyViewItem {
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
