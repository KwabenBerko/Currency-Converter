package com.kwabenaberko.currencyconverter

enum class RoundingMethod {
    FLOOR,
    HALF_EVEN
}

expect class DecimalFormatter(
    maximumFractionDigits: Int = 2,
    roundingMethod: RoundingMethod = RoundingMethod.HALF_EVEN
) {
    fun format(value: Double): String
    fun parse(text: String): Double
}