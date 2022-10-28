package com.kwabenaberko.currencyconverter.android

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import com.kwabenaberko.currencyconverter.android.converter.model.ConversionMode

inline fun <reified T> Any.runIf(block: (T) -> Unit) {
    if (this is T) {
        block(this)
    }
}

fun Color.isLight(): Boolean {
    return ColorUtils.calculateLuminance(this.toArgb()) > 0.5
}

fun useRedTheme(conversionMode: ConversionMode): Boolean {
    return conversionMode == ConversionMode.FIRST_MONEY_TO_SECOND_MONEY
}
