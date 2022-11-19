package com.kwabenaberko.currencyconverter.domain.usecase

@Suppress("FUN_INTERFACE_WITH_SUSPEND_FUNCTION")
fun interface Sync {
    suspend operator fun invoke(): Boolean
}
