package com.kwabenaberko.currencyconverter.android.sync

import androidx.lifecycle.viewModelScope
import com.kwabenaberko.currencyconverter.android.BaseViewModel
import com.kwabenaberko.currencyconverter.domain.usecase.HasCompletedInitialSync
import com.kwabenaberko.currencyconverter.domain.usecase.Sync
import kotlinx.coroutines.launch

class SyncViewModel(
    private val hasCompletedInitialSync: HasCompletedInitialSync,
    private val sync: Sync
) : BaseViewModel<SyncViewModel.State>(State.Idle) {

    fun doSync() {
        viewModelScope.launch {
            if (hasCompletedInitialSync()) {
                setState(State.SyncSuccess)
                return@launch
            }

            setState(State.Syncing)

            when (sync()) {
                true -> setState(State.SyncSuccess)
                false -> setState(State.SyncError)
            }
        }
    }

    sealed class State {
        object Idle : State()
        object Syncing : State()
        object SyncError : State()
        object SyncSuccess : State()
    }
}
