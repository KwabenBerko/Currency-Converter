package com.kwabenaberko.converter.presentation.viewmodel

import com.kwabenaberko.converter.presentation.Amount
import com.kwabenaberko.converter.presentation.AmountInputEngine
import com.rickclephas.kmm.viewmodel.KMMViewModel
import com.rickclephas.kmm.viewmodel.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class KeypadViewModel : BaseViewModel<Amount>(Amount()) {

    private val amountEngine = AmountInputEngine()

    init {
        amountEngine.amount
            .onEach { value ->
                setState(value)
            }.launchIn(scope)
    }

    fun add(value: String) {
        amountEngine.add(value)
    }

    fun pop() {
        amountEngine.pop()
    }
}
