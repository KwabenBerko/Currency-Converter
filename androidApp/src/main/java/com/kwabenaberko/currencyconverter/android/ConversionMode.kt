package com.kwabenaberko.currencyconverter.android

sealed class ConversionMode {
    object Default: ConversionMode()
    object Inverse: ConversionMode()
}
