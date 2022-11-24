package com.kwabenaberko.convertertest.testdouble

import com.kwabenaberko.converter.domain.model.Currency
import com.kwabenaberko.converter.domain.model.Money
import com.kwabenaberko.converter.domain.usecase.ConvertMoney

class FakeConvertMoney : ConvertMoney {
    lateinit var result: Money

    override suspend fun invoke(money: Money, targetCurrency: Currency): Money {
        return result
    }
}
