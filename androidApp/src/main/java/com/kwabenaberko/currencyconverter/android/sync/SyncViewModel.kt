package com.kwabenaberko.currencyconverter.android.sync

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kwabenaberko.currencyconverter.android.BaseViewModel
import com.kwabenaberko.currencyconverter.domain.usecase.Sync
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SyncViewModel(
    private val sync: Sync
) : BaseViewModel<SyncViewModel.State>(State.Idle) {

    init {
        startSync()
    }

    fun startSync() {
        viewModelScope.launch {

            setState(State.Syncing)
            delay(5000L)

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
        private val sync: Sync
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SyncViewModel(sync) as T
        }
    }
}
