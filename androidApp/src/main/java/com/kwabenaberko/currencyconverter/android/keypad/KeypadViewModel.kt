package com.kwabenaberko.currencyconverter.android.keypad

import androidx.lifecycle.ViewModel
import com.kwabenaberko.converter.presentation.AmountInputEngine

class KeypadViewModel : ViewModel() {

    private val amountEngine = AmountInputEngine()
    val state = amountEngine.amount

    fun append(value: Char) {
        amountEngine.add(value)
    }

    fun undo() {
        amountEngine.pop()
    }
}
