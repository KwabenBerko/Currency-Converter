package com.kwabenaberko.currencyconverter

import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterDecimalStyle
import platform.Foundation.NSNumberFormatterRoundDown
import platform.Foundation.NSNumberFormatterRoundHalfUp

actual class DecimalFormatter actual constructor(
    roundingMethod: RoundingMethod,
    maximumFractionDigits: Int?,
    maximumSignificantDigits: Int?
) {
    private val formatter = NSNumberFormatter().apply {
        this.roundingMode = when (roundingMethod) {
            RoundingMethod.HALF_UP -> NSNumberFormatterRoundHalfUp
            RoundingMethod.DOWN -> NSNumberFormatterRoundDown
        }
        this.numberStyle = NSNumberFormatterDecimalStyle
        this.minimumFractionDigits = 0.toULong()
        maximumFractionDigits?.let {
            this.maximumFractionDigits = maximumFractionDigits.toULong()
        }
        maximumSignificantDigits?.let {
            this.maximumSignificantDigits = maximumSignificantDigits.toULong()
            this.usesSignificantDigits = true
        }
    }

    actual fun format(value: Double): String {
        return formatter.stringFromNumber(NSNumber(value)) ?: ""
    }

    actual fun parse(text: String): Double {
        return formatter.numberFromString(text)?.doubleValue ?: 0.0
    }
}
