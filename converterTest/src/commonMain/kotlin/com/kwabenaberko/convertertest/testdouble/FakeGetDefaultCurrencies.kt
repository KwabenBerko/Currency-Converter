package com.kwabenaberko.convertertest.testdouble

import com.kwabenaberko.converter.domain.model.DefaultCurrencies
import com.kwabenaberko.converter.domain.usecase.GetDefaultCurrencies

class FakeGetDefaultCurrencies : GetDefaultCurrencies {
    lateinit var result: DefaultCurrencies

    override suspend fun invoke(): DefaultCurrencies {
        return result
    }
}
