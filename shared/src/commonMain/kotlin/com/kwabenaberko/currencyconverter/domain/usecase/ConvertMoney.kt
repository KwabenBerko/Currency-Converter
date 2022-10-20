package com.kwabenaberko.currencyconverter.domain.usecase

import com.kwabenaberko.currencyconverter.domain.model.Currency
import com.kwabenaberko.currencyconverter.domain.model.Money
import com.kwabenaberko.currencyconverter.toPlaces
import com.kwabenaberko.currencyconverter.toSignificantDigits

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
    val convertedAmount = rate.times(amount)
    val roundedAmount = if (convertedAmount < 1) {
        convertedAmount.toSignificantDigits(digits = 2)
    } else {
        convertedAmount.toPlaces(places = 2)
    }
    setDefaultCurrencies(baseCode, targetCode)
    return Money(targetCurrency, roundedAmount)
}

typealias ConvertMoney = suspend (
    money: Money,
    targetCurrency: Currency,
) -> Money
