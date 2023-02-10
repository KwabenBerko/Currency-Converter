package com.kwabenaberko.converter.presentation.viewmodel

import com.rickclephas.kmm.viewmodel.KMMViewModel
import com.rickclephas.kmm.viewmodel.MutableStateFlow
import com.rickclephas.kmm.viewmodel.coroutineScope
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseViewModel<T : Any>(initialState: T) : KMMViewModel() {
    protected val scope = viewModelScope.coroutineScope
    private val _state = MutableStateFlow(viewModelScope, initialState)
    val state = _state.asStateFlow()

    protected fun setState(newState: T) {
        _state.value = newState
    }

    protected fun getState(): T {
        return _state.value
    }

    protected inline fun <reified T> runIf(block: (T) -> Unit) {
        val currentState = getState()
        if (currentState is T) {
            block(currentState)
        }
    }
}
