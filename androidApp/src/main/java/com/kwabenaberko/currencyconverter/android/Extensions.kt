package com.kwabenaberko.currencyconverter.android

inline fun <reified T> Any.runIf(block: (T) -> Unit) {
    if (this is T) {
        block(this)
    }
}
