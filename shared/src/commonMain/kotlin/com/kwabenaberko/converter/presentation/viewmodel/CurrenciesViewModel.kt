package com.kwabenaberko.converter.presentation.viewmodel

import com.kwabenaberko.converter.domain.model.Currency
import com.kwabenaberko.converter.domain.usecase.GetCurrencies
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class CurrenciesViewModel(
    private val selectedCurrencyCode: String,
    private val getCurrencies: GetCurrencies
) : BaseViewModel<CurrenciesViewModel.State>(State.Idle) {

    private val filterQueryFlow = MutableStateFlow("")

    init {
        loadCurrencies()
    }

    fun filterCurrencies(query: String) {
        scope.launch {
            filterQueryFlow.emit(query)
        }
    }

    private fun loadCurrencies() {
        filterQueryFlow
            .debounce { debounceTimeoutInMillis() }
            .flatMapLatest { query -> getCurrencies(query) }
            .onEach(::handleResult)
            .launchIn(scope)
    }

    private fun handleResult(currencies: List<Currency>) {
        val groupedCurrencies = currencies
            .groupBy { currency -> currency.name.first().toString() }
            .toPersistentMap()

        runIf<State.Idle> {
            val selectedCurrency = currencies
                .first { currency -> currency.code == selectedCurrencyCode }

            val newState = State.Content(
                selectedCurrency = selectedCurrency,
                currencies = groupedCurrencies
            )
            setState(newState)
        }

        runIf<State.Content> { currentState ->
            val newState = currentState.copy(currencies = groupedCurrencies)
            setState(newState)
        }
    }

    private fun debounceTimeoutInMillis(): Long {
        return if (getState() is State.Idle || filterQueryFlow.value.isEmpty()) {
            0L
        } else {
            300L
        }
    }

    sealed class State {
        object Idle : State()
        data class Content(
            val selectedCurrency: Currency,
            val currencies: Map<String, List<Currency>>,
        ) : State()
    }

    companion object {
        private val GHS = Currency("GHS", name = "Ghanaian Cedi", symbol = "GH₵")
        private val USD = Currency("USD", name = "United States Dollar", symbol = "$")
        private val NGN = Currency("NGN", name = "Nigerian Naira", symbol = "₦")
        val mockContentState = State.Content(
            selectedCurrency = GHS,
            currencies = buildMap {
                put("G", listOf(GHS))
                put("U", listOf(USD))
                put("N", listOf(NGN))
            }
        )
    }
}
