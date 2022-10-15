package com.kwabenaberko.currencyconverter.android.currencies

import androidx.lifecycle.viewModelScope
import com.kwabenaberko.currencyconverter.android.BaseViewModel
import com.kwabenaberko.currencyconverter.domain.model.Currency
import com.kwabenaberko.currencyconverter.domain.usecase.GetCurrencies
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class CurrenciesViewModel(
    private val getCurrencies: GetCurrencies
) : BaseViewModel<CurrenciesViewModel.State>(State.Idle) {

    init {
        loadCurrencies(query = null)
    }

    fun loadCurrencies(query: String?) {
        getCurrencies(query)
            .onEach { currencies ->
                val groupedCurrencies = currencies
                    .groupBy { currency -> currency.name.first() }
                    .toPersistentMap()

                val newState = State.Content(
                    query = query ?: "",
                    currencies = groupedCurrencies
                )

                setState(newState)
            }
            .launchIn(viewModelScope)
    }

    sealed class State {
        object Idle : State()
        data class Content(
            val query: String,
            val currencies: Map<Char, List<Currency>>
        ) : State()
    }
}
