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

class KeypadViewModel : KMMViewModel() {

    private val scope = viewModelScope.coroutineScope
    private val amountEngine = AmountInputEngine()
    private val _amount = MutableStateFlow(Amount())
    val amount = _amount.asStateFlow()

    init {
        amountEngine.amount
            .onEach { value ->
                _amount.update { value }
            }.launchIn(scope)
    }

    fun add(value: Char) {
        amountEngine.add(value)
    }

    fun pop() {
        amountEngine.pop()
    }
}
