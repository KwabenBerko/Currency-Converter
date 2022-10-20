package com.kwabenaberko.currencyconverter.android.sync

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kwabenaberko.currencyconverter.android.BaseViewModel
import com.kwabenaberko.currencyconverter.domain.usecase.HasCompletedInitialSync
import com.kwabenaberko.currencyconverter.domain.usecase.Sync
import kotlinx.coroutines.launch

class SyncViewModel(
    private val hasCompletedInitialSync: HasCompletedInitialSync,
    private val sync: Sync
) : BaseViewModel<SyncViewModel.State>(State.Idle) {

    init {
        startSync()
    }

    fun startSync() {
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

    class Factory(
        private val hasCompletedInitialSync: HasCompletedInitialSync,
        private val sync: Sync
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SyncViewModel(hasCompletedInitialSync, sync) as T
        }
    }
}
