package com.kwabenaberko.currencyconverter.android.currencies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kwabenaberko.converter.domain.model.Currency
import com.kwabenaberko.converter.domain.usecase.GetCurrencies
import com.kwabenaberko.currencyconverter.android.BaseViewModel
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
    private val getCurrencies: GetCurrencies
) : com.kwabenaberko.currencyconverter.android.BaseViewModel<CurrenciesViewModel.State>(State.Idle) {

    private val filterQueryFlow = MutableStateFlow("")

    init {
        loadCurrencies()
    }

    fun filterCurrencies(query: String) {
        viewModelScope.launch {
            filterQueryFlow.emit(query)
        }
    }

    private fun loadCurrencies() {
        filterQueryFlow
            .debounce { debounceTimeoutInMillis() }
            .flatMapLatest { query -> getCurrencies(query) }
            .onEach { currencies ->
                val groupedCurrencies = currencies
                    .groupBy { currency -> currency.name.first() }
                    .toPersistentMap()

                setState(State.Content(groupedCurrencies))
            }.launchIn(viewModelScope)
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
        data class Content(val currencies: Map<Char, List<Currency>>) : State()
    }

    class Factory(private val getCurrencies: GetCurrencies) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CurrenciesViewModel(getCurrencies) as T
        }
    }
}
