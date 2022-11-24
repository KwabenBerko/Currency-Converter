package com.kwabenaberko.converter.domain.usecase

import com.kwabenaberko.converter.domain.model.DefaultCurrencies

@Suppress("FUN_INTERFACE_WITH_SUSPEND_FUNCTION")
fun interface GetDefaultCurrencies {
    suspend operator fun invoke(): DefaultCurrencies
}
