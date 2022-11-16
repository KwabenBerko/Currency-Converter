package com.kwabenaberko.currencyconverter.presentation

import com.kwabenaberko.currencyconverter.presentation.AmountInputEngine.Amount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface AmountInputEngine {
    val amount: StateFlow<Amount>

    fun amount(): Amount
    fun append(character: Char)
    fun undo()

    data class Amount(
        val text: String = "",
        val isValid: Boolean = false
    )

    companion object
}

fun AmountInputEngine.Companion.create(): AmountInputEngine {
    return RealAmountInputEngine()
}

class RealAmountInputEngine : AmountInputEngine {
    private val _amount = MutableStateFlow(Amount())
    override val amount = _amount.asStateFlow()

    override fun amount(): Amount {
        return _amount.value
    }

    override fun append(character: Char) {
        val amount = amount()
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

        if (character.isDigit() || character.isDot()) {
            val shouldPrependZero = amountText.isEmpty() && character.isDot()
            val newAmountText = (if (shouldPrependZero) amountText.plus(ZERO) else amountText)
                .plus(character)
                .trim()

            val newAmount = Amount(
                text = newAmountText,
                isValid = newAmountText.isValidAmount()
            )

            setAmount(newAmount)
        }
    }

    override fun undo() {
        val amount = amount()
        val amountText = amount.text
        if (amountText.isNotEmpty()) {
            val newAmountValue = amountText
                .substring(0, amountText.length - 1)
                .trim()

            val newAmount = Amount(
                text = newAmountValue,
                isValid = newAmountValue.isValidAmount()
            )
            setAmount(newAmount)
        }
    }

    private fun setAmount(newAmount: Amount) {
        _amount.value = newAmount
    }

    private fun Char.isDot(): Boolean {
        return this == DOT
    }

    private fun String.containsDot(): Boolean {
        return this.contains(DOT)
    }

    private fun String.endsWithDot(): Boolean {
        return this.last() == DOT
    }

    private fun String.endsWithZero(): Boolean {
        return this.last() == ZERO
    }

    private fun String.isValidAmount(): Boolean {
        return this.isNotEmpty() && !this.endsWithDot()
    }

    companion object {
        const val DOT = '.'
        const val ZERO = '0'
    }
}
