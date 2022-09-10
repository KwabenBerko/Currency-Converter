package com.kwabenaberko.sharedtest.testdouble

import com.kwabenaberko.currencyconverter.domain.model.Currency
import com.kwabenaberko.currencyconverter.domain.model.Money
import com.kwabenaberko.currencyconverter.domain.usecase.ConvertMoney

class FakeConvertMoney : ConvertMoney {
    lateinit var result: Money

    override suspend fun invoke(money: Money, targetCurrency: Currency): Money {
        return result
    }
}
