package com.kwabenaberko.currencyconverter.domain.usecase

@Suppress("FUN_INTERFACE_WITH_SUSPEND_FUNCTION")
fun interface SetDefaultCurrencies {
    suspend operator fun invoke(baseCode: String, targetCode: String)
}
