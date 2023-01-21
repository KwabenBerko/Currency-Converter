package com.kwabenaberko.converter.testdouble

import com.kwabenaberko.converter.domain.model.DefaultCurrencies
import com.kwabenaberko.converter.domain.usecase.GetDefaultCurrencies
import kotlinx.coroutines.flow.Flow

class FakeGetDefaultCurrencies : GetDefaultCurrencies {
    lateinit var result: Flow<DefaultCurrencies>

    override fun invoke(): Flow<DefaultCurrencies> {
        return result
    }
}
