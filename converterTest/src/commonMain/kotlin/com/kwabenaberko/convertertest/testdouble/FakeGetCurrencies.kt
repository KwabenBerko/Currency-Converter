package com.kwabenaberko.convertertest.testdouble

import com.kwabenaberko.converter.domain.model.Currency
import com.kwabenaberko.converter.domain.usecase.GetCurrencies
import kotlinx.coroutines.flow.Flow

class FakeGetCurrencies : GetCurrencies {
    lateinit var result: Flow<List<Currency>>

    override fun invoke(filter: String?): Flow<List<Currency>> {
        return result
    }
}
