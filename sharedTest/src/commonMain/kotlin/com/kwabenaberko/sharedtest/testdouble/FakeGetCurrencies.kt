package com.kwabenaberko.sharedtest.testdouble

import com.kwabenaberko.currencyconverter.domain.model.Currency
import com.kwabenaberko.currencyconverter.domain.usecase.GetCurrencies
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class FakeGetCurrencies : GetCurrencies {
    val result = MutableSharedFlow<List<Currency>>()

    override fun invoke(): Flow<List<Currency>> {
        return result
    }
}
