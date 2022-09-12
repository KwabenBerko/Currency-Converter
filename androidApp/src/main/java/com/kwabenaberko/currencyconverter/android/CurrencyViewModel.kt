package com.kwabenaberko.currencyconverter.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kwabenaberko.currencyconverter.domain.model.Currency
import com.kwabenaberko.currencyconverter.domain.model.CurrencyFilter
import com.kwabenaberko.currencyconverter.domain.model.SyncStatus
import com.kwabenaberko.currencyconverter.domain.usecase.GetCurrencies
import com.kwabenaberko.currencyconverter.domain.usecase.GetSyncStatus
import com.kwabenaberko.currencyconverter.domain.usecase.HasCompletedInitialSync
import com.kwabenaberko.currencyconverter.domain.usecase.Sync
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class CurrencyViewModel constructor(
    private val hasCompletedInitialSync: HasCompletedInitialSync,
    private val getSyncStatus: GetSyncStatus,
    private val sync: Sync,
    private val getCurrencies: GetCurrencies,
    private val dispatcherProvider: DispatcherProvider = RealDispatcherProvider()
) : ViewModel() {

    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    private val searchQueryFlow = MutableStateFlow("")

    init {
        observeSyncStatus()
        observeCurrencies()

        viewModelScope.launch(dispatcherProvider.io) {
            if (!hasCompletedInitialSync()) {
                sync()
            }
        }
    }

    fun search(query: String) {
        _state.update { currentState ->
            currentState.copy(searchQuery = query)
        }
        searchQueryFlow.update { query }
    }

    private fun observeSyncStatus() {
        getSyncStatus()
            .flowOn(dispatcherProvider.io)
            .onEach { status ->
                _state.update { currentState ->
                    currentState.copy(syncStatus = status)
                }
            }
            .launchIn(viewModelScope.plus(dispatcherProvider.io))
    }

    private fun observeCurrencies() {
        searchQueryFlow
            .debounce(timeoutMillis = 500)
            .flatMapLatest { query ->
                getCurrencies(makeFilter(query))
            }
            .onEach { currencies ->
                _state.update { currentState ->
                    val grouped = currencies
                        .groupBy { currency -> currency.name.first() }
                        .toPersistentMap()

                    currentState.copy(currencies = grouped)
                }
            }
            .flowOn(dispatcherProvider.io)
            .launchIn(viewModelScope.plus(dispatcherProvider.io))
    }

    private fun makeFilter(query: String): CurrencyFilter? {
        return if (query.isBlank()) {
            null
        } else {
            CurrencyFilter(name = query)
        }
    }

    data class State(
        val syncStatus: SyncStatus? = null,
        val searchQuery: String = "",
        val currencies: ImmutableMap<Char, List<Currency>> = persistentMapOf()
    )
}
