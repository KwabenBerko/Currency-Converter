package com.kwabenaberko.currencyconverter.android.keypad

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kwabenaberko.converter.presentation.Amount
import com.kwabenaberko.converter.presentation.AmountInputEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class KeypadViewModel : ViewModel() {

    private val amountEngine = AmountInputEngine()
    private val _amount = MutableStateFlow(Amount())
    val amount = _amount.asStateFlow()

    init {
        amountEngine.amount
            .onEach { value ->
                _amount.update { value }
            }.launchIn(viewModelScope)
    }

    fun add(value: Char) {
        amountEngine.add(value)
    }

    fun pop() {
        amountEngine.pop()
    }
}
