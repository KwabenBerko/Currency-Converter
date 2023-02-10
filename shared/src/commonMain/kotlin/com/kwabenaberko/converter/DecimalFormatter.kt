package com.kwabenaberko.converter

enum class RoundingMethod {
    HALF_UP,
    DOWN
}

expect class DecimalFormatter constructor(
    roundingMethod: RoundingMethod? = RoundingMethod.HALF_UP,
    maximumFractionDigits: Int? = null,
    maximumSignificantDigits: Int? = null,
) {
    fun format(value: Double): String
    fun parse(text: String): Double
}
