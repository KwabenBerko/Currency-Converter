package com.kwabenaberko.currencyconverter.android

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.reflect.KClass

abstract class BaseViewModel<T>(initialState: T) : ViewModel() {
    private val _state = MutableStateFlow(initialState)
    val state = _state.asStateFlow()

    protected fun setState(newState: T) {
        _state.value = newState
    }

    protected fun getState(): T {
        return _state.value
    }

    protected inline fun <reified S> runIf(block: (S) -> Unit){
        val currentState = getState()
        if (currentState is S) {
            block(currentState)
        }
    }
}
