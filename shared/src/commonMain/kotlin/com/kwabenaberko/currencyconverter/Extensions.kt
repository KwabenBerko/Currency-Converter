package com.kwabenaberko.currencyconverter

import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

fun Double.round(): Double {
    val log10 = floor(log10(this))
    val div = if (log10 < 0.0) 10.0.pow(1 - log10) else 100.0
    return kotlin.math.round(this.times(div)).div(div)
}
