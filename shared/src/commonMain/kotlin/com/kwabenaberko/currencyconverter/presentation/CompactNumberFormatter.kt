package com.kwabenaberko.currencyconverter.presentation

import com.kwabenaberko.currencyconverter.DecimalFormatter
import com.kwabenaberko.currencyconverter.RoundingMethod
import kotlin.math.abs

class CompactNumberFormatter {

    fun format(number: Number): String {
        val value = abs(number.toDouble())
        return when {
            value >= 1_000_000_000_000 -> {
                "${formatter().format(value.div(1_000_000_000_000))}T"
            }
            value >= 1_000_000_000 -> {
                "${formatter().format(value.div(1_000_000_000))}B"
            }
            value >= 1_000_000 -> {
                "${formatter().format(value.div(1_000_000))}M"
            }
            value >= 1_000 -> {
                "${formatter().format(value.div(1_000))}K"
            }
            else -> {
                formatter(maximumFractionDigits = 6, roundingMethod = null).format(value)
            }
        }
    }

    private fun formatter(
        maximumFractionDigits: Int = 1,
        roundingMethod: RoundingMethod? = RoundingMethod.DOWN
    ): DecimalFormatter {
        return DecimalFormatter(
            maximumFractionDigits = maximumFractionDigits,
            roundingMethod = roundingMethod
        )
    }
}
