package com.kwabenaberko.currencyconverter

import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter

actual class DecimalFormatter actual constructor(
    maximumFractionDigits: Int,
    roundingMethod: RoundingMethod
) {
    private val formatter = NSNumberFormatter().apply {
        this.minimumFractionDigits = 0.toULong()
        this.maximumFractionDigits = maximumFractionDigits.toULong()
        this.roundingMode = when(roundingMethod){
            RoundingMethod.FLOOR -> 1.toULong()
            RoundingMethod.HALF_EVEN -> 4.toULong()
        }
        this.numberStyle = 1.toULong()
    }

    actual fun format(value: Double): String {
        return formatter.stringFromNumber(NSNumber(value)) ?: ""
    }

    actual fun parse(text: String): Double {
        return formatter.numberFromString(text)?.doubleValue ?: 0.0
    }
}
