package com.kwabenaberko.converter.presentation.viewmodel

import com.kwabenaberko.converter.domain.usecase.Sync
import kotlinx.coroutines.launch

class SyncViewModel(
    private val sync: Sync
) : BaseViewModel<SyncViewModel.State>(State.Idle) {

    init {
        startSync()
    }

    fun startSync() {
        scope.launch {

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
