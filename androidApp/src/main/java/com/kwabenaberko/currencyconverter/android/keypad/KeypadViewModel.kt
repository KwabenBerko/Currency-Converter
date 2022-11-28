package com.kwabenaberko.currencyconverter.android.keypad

import androidx.lifecycle.ViewModel
import com.kwabenaberko.converter.presentation.AmountInputEngine

class KeypadViewModel : ViewModel() {

    private val amountEngine = AmountInputEngine()
    val amount = amountEngine.amount

    fun add(value: Char) {
        amountEngine.add(value)
    }

    fun pop() {
        amountEngine.pop()
    }
}
