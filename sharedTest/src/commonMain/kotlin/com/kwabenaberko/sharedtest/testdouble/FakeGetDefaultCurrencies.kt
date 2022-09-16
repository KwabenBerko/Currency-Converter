package com.kwabenaberko.sharedtest.testdouble

import com.kwabenaberko.currencyconverter.domain.model.DefaultCurrencies
import com.kwabenaberko.currencyconverter.domain.usecase.GetDefaultCurrencies

class FakeGetDefaultCurrencies : GetDefaultCurrencies {
    lateinit var result: DefaultCurrencies

    override suspend fun invoke(): DefaultCurrencies {
        return result
    }
}
