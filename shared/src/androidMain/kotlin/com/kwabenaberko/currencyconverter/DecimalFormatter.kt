package com.kwabenaberko.currencyconverter

import java.math.RoundingMode

actual class DecimalFormatter actual constructor(
    maximumFractionDigits: Int,
    roundingMethod: RoundingMethod
) {
    private val formatter = java.text.DecimalFormat().apply {
        this.minimumFractionDigits = 0
        this.maximumFractionDigits = maximumFractionDigits
        this.roundingMode = when (roundingMethod) {
            RoundingMethod.FLOOR -> RoundingMode.FLOOR
            RoundingMethod.HALF_EVEN -> RoundingMode.HALF_EVEN
        }
    }

    actual fun format(value: Double): String {
        return formatter.format(value)
    }

    actual fun parse(text: String): Double {
        return formatter.parse(text)?.toDouble() ?: 0.0
    }
}