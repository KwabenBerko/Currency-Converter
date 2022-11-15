package com.kwabenaberko.currencyconverter.android.keypad

import androidx.lifecycle.ViewModel
import com.kwabenaberko.currencyconverter.presentation.AmountInputEngine
import com.kwabenaberko.currencyconverter.presentation.create

class KeypadViewModel : ViewModel() {

    private val amountEngine = AmountInputEngine.create()
    val state = amountEngine.amount

    fun append(value: Char) {
        amountEngine.append(value)
    }

    fun undo() {
        amountEngine.undo()
    }
}
