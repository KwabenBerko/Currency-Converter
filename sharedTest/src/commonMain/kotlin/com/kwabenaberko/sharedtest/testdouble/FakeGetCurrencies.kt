package com.kwabenaberko.sharedtest.testdouble

import com.kwabenaberko.currencyconverter.domain.model.Currency
import com.kwabenaberko.currencyconverter.domain.usecase.GetCurrencies
import kotlinx.coroutines.flow.Flow

class FakeGetCurrencies : GetCurrencies {
    lateinit var result: Flow<List<Currency>>

    override fun invoke(filter: String?): Flow<List<Currency>> {
        return result
    }
}
