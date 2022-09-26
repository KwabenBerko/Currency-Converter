package com.kwabenaberko.sharedtest.testdouble

import com.kwabenaberko.currencyconverter.domain.model.Currency
import com.kwabenaberko.currencyconverter.domain.model.CurrencyFilter
import com.kwabenaberko.currencyconverter.domain.usecase.GetCurrencies
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class FakeGetCurrencies : GetCurrencies {
    lateinit var result: Flow<List<Currency>>

    override fun invoke(filter: CurrencyFilter?): Flow<List<Currency>> {
        return result
    }
}
