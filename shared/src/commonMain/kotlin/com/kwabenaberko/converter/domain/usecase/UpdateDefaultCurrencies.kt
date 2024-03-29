package com.kwabenaberko.converter.domain.usecase

@Suppress("FUN_INTERFACE_WITH_SUSPEND_FUNCTION")
fun interface UpdateDefaultCurrencies {
    suspend operator fun invoke(baseCode: String, targetCode: String)
}
