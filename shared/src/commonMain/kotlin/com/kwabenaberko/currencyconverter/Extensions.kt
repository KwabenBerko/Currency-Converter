package com.kwabenaberko.currencyconverter

fun Double.round(): Double {
    val formatter = DecimalFormatter()
    val formatted = formatter.format(this)
    return formatter.parse(formatted)
}
