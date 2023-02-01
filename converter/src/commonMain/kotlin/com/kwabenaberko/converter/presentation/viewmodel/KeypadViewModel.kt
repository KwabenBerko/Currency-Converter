package com.kwabenaberko.converter.presentation.viewmodel

import com.kwabenaberko.converter.presentation.viewmodel.KeypadViewModel.State

class KeypadViewModel : BaseViewModel<State>(State()) {
    fun append(character: String) {
        val amount = getState()
        val amountText = amount.text

        if (
            character.isDot() &&
            amountText.containsDot()
        ) {
            return
        }

        if (
            amountText.containsDot() &&
            amountText.endsWithZero() &&
            character == ZERO
        ) {
            return
        }

        if (character.first().isDigit() || character.isDot()) {
            val newAmountText = if (amountText == ZERO && character != ZERO) character
            else {
                val shouldPrependZero = amountText.isEmpty() && character.isDot()
                (if (shouldPrependZero) amountText.plus(ZERO) else amountText)
                    .plus(character)
                    .trim()
            }

            val newState = State(
                text = newAmountText,
                isValid = newAmountText.isValidAmount()
            )

            setState(newState)
        }
    }

    fun removeLast() {
        val amount = getState()
        val amountText = amount.text
        if (amountText.isNotEmpty()) {
            val newAmountValue = amountText
                .substring(0, amountText.length - 1)
                .trim()

            val newState = State(
                text = newAmountValue,
                isValid = newAmountValue.isValidAmount()
            )
            setState(newState)
        }
    }

    private fun String.isDot(): Boolean {
        return this == DOT
    }

    private fun String.containsDot(): Boolean {
        return this.contains(DOT)
    }

    private fun String.endsWithDot(): Boolean {
        return this.last().toString() == DOT
    }

    private fun String.endsWithZero(): Boolean {
        return this.last().toString() == ZERO
    }

    private fun String.isValidAmount(): Boolean {
        return this.isNotEmpty() && !this.endsWithDot()
    }

    companion object {
        const val DOT = "."
        const val ZERO = "0"
    }

    data class State(
        val text: String = "",
        val isValid: Boolean = false
    )
}
