package com.kwabenaberko.currencyconverter.android

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseViewModel<T>(initialState: T) : ViewModel() {
    private val _state = MutableStateFlow(initialState)
    val state = _state.asStateFlow()

    protected fun setState(newState: T) {
        _state.value = newState
    }

    protected fun getState(): T {
        return _state.value
    }
}
