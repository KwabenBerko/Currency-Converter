package com.kwabenaberko.sharedtest.testdouble

import com.kwabenaberko.currencyconverter.domain.model.DefaultCurrencies
import com.kwabenaberko.currencyconverter.domain.usecase.GetDefaultCurrencies
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class FakeGetDefaultCurrencies : GetDefaultCurrencies {
    val result = MutableSharedFlow<Flow<DefaultCurrencies>>()

    override fun invoke(): Flow<DefaultCurrencies> {
        TODO("Not yet implemented")
    }
}
