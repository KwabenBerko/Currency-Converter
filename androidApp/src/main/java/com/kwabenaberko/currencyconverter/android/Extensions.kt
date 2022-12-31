package com.kwabenaberko.currencyconverter.android

import com.kwabenaberko.currencyconverter.android.converter.model.ConversionMode

fun useRedTheme(conversionMode: ConversionMode): Boolean {
    return conversionMode == ConversionMode.FIRST_TO_SECOND
}
