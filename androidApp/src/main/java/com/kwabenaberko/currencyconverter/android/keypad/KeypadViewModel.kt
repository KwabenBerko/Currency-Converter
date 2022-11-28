package com.kwabenaberko.currencyconverter.android.keypad

import androidx.lifecycle.ViewModel
import com.kwabenaberko.converter.presentation.RealAmountInputEngine

class KeypadViewModel : ViewModel() {

    private val amountEngine = RealAmountInputEngine()
    val state = amountEngine.amount

    fun append(value: Char) {
        amountEngine.add(value)
    }

    fun undo() {
        amountEngine.pop()
    }
}
