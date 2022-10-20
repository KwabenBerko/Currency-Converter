package com.kwabenaberko.currencyconverter.presentation

import com.kwabenaberko.currencyconverter.DecimalFormatter
import com.kwabenaberko.currencyconverter.RoundingMethod
import kotlin.math.abs

class CompactNumberFormatter {
    private val formatter = DecimalFormatter(
        maximumFractionDigits = 1,
        roundingMethod = RoundingMethod.DOWN
    )

    fun format(number: Number): String {
        val value = abs(number.toDouble())
        return when {
            value >= 1_000_000_000_000 -> {
                println(999_999.9.div(1_000))
                "${formatter.format(value.div(1_000_000_000_000))}T"
            }
            value >= 1_000_000_000 -> {
                "${formatter.format(value.div(1_000_000_000))}B"
            }
            value >= 1_000_000 -> {
                "${formatter.format(value.div(1_000_000))}M"
            }
            value >= 1_000 -> {
                "${formatter.format(value.div(1_000))}K"
            }
            else -> "$value"
        }
    }
}
