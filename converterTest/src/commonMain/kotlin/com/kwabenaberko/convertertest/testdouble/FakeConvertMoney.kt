package com.kwabenaberko.convertertest.testdouble

import com.kwabenaberko.converter.domain.model.Currency
import com.kwabenaberko.converter.domain.model.Money
import com.kwabenaberko.converter.domain.usecase.ConvertMoney
import kotlinx.coroutines.flow.Flow

class FakeConvertMoney : ConvertMoney {
    lateinit var result: Flow<Money>

    override fun invoke(money: Money, targetCurrency: Currency): Flow<Money> {
        return result
    }
}
