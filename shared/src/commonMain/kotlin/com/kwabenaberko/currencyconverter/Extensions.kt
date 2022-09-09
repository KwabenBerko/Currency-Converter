package com.kwabenaberko.currencyconverter

fun Double.round(places: Int): Double {
    val formatter = DecimalFormatter(maximumFractionDigits = places)
    val formatted = formatter.format(this)
    return formatter.parse(formatted)
}
