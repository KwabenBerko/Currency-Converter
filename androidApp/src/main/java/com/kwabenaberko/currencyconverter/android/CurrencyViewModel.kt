package com.kwabenaberko.currencyconverter.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kwabenaberko.currencyconverter.domain.model.SyncStatus
import com.kwabenaberko.currencyconverter.domain.usecase.GetSyncStatus
import com.kwabenaberko.currencyconverter.domain.usecase.HasCompletedInitialSync
import com.kwabenaberko.currencyconverter.domain.usecase.Sync
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

class CurrencyViewModel constructor(
    private val hasCompletedInitialSync: HasCompletedInitialSync,
    private val getSyncStatus: GetSyncStatus,
    private val sync: Sync,
    private val dispatcherProvider: DispatcherProvider = RealDispatcherProvider()
) : ViewModel() {

    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    init {
        getSyncStatus()
            .flowOn(dispatcherProvider.io)
            .onEach { status ->
                _state.update { currentState ->
                    currentState.copy(syncStatus = status)
                }
            }
            .launchIn(viewModelScope.plus(dispatcherProvider.io))

        viewModelScope.launch(dispatcherProvider.io) {
            if (!hasCompletedInitialSync()) {
                sync()
            }
        }
    }

    data class State(
        val syncStatus: SyncStatus? = null
    )
}
