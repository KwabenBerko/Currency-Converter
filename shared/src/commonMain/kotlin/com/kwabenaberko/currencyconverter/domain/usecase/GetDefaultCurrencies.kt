package com.kwabenaberko.currencyconverter.domain.usecase

import com.kwabenaberko.currencyconverter.domain.model.DefaultCurrencies

@Suppress("FUN_INTERFACE_WITH_SUSPEND_FUNCTION")
fun interface GetDefaultCurrencies {
    suspend operator fun invoke(): DefaultCurrencies
}
