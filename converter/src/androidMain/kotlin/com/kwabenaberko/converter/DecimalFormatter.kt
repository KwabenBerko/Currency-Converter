package com.kwabenaberko.converter

import com.ibm.icu.text.DecimalFormat
import java.math.RoundingMode

actual class DecimalFormatter actual constructor(
    roundingMethod: RoundingMethod?,
    maximumFractionDigits: Int?,
    maximumSignificantDigits: Int?
) {
    private val formatter = DecimalFormat().apply {
        this.minimumFractionDigits = 0
        roundingMethod?.let {
            this.roundingMode = when (roundingMethod) {
                RoundingMethod.HALF_UP -> RoundingMode.HALF_UP.ordinal
                RoundingMethod.DOWN -> RoundingMode.DOWN.ordinal
            }
        }
        maximumFractionDigits?.let {
            this.maximumFractionDigits = maximumFractionDigits
        }
        maximumSignificantDigits?.let {
            this.maximumSignificantDigits = maximumSignificantDigits
            this.setSignificantDigitsUsed(true)
        }
    }

    actual fun format(value: Double): String {
        return formatter.format(value)
    }

    actual fun parse(text: String): Double {
        return formatter.parse(text)?.toDouble() ?: 0.0
    }
}