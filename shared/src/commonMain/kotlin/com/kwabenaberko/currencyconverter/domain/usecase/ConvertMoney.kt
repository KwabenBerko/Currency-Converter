package com.kwabenaberko.currencyconverter.domain.usecase

import com.kwabenaberko.currencyconverter.domain.model.Currency
import com.kwabenaberko.currencyconverter.domain.model.Money
import com.kwabenaberko.currencyconverter.round

suspend fun convertMoney(
    getRate: GetRate,
    setDefaultCurrencies: SetDefaultCurrencies,
    money: Money,
    targetCurrency: Currency,
): Money {
    val (baseCurrency, amount) = money
    val baseCode = baseCurrency.code
    val targetCode = targetCurrency.code

    val rate = getRate(baseCode, targetCode)
    val convertedAmount = rate.times(amount).round()
    setDefaultCurrencies(baseCode, targetCode)
    return Money(targetCurrency, convertedAmount)
}

typealias ConvertMoney = suspend (
    money: Money,
    targetCurrency: Currency,
) -> Money
