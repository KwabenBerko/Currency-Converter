package com.kwabenaberko.currencyconverter.android

import com.kwabenaberko.currencyconverter.android.converter.model.ConversionMode

inline fun <reified T> Any.runIf(block: (T) -> Unit) {
    if (this is T) {
        block(this)
    }
}

fun useRedTheme(conversionMode: ConversionMode): Boolean {
    return conversionMode == ConversionMode.FIRST_MONEY_TO_SECOND_MONEY
}
