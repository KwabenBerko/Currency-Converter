package com.kwabenaberko.converter.domain.usecase.implementation

import com.kwabenaberko.converter.domain.model.Currency
import com.kwabenaberko.converter.domain.model.Money
import com.kwabenaberko.converter.domain.usecase.ConvertMoney
import com.kwabenaberko.converter.domain.usecase.GetRate
import com.kwabenaberko.converter.domain.usecase.UpdateDefaultCurrencies
import com.kwabenaberko.converter.toPlaces
import com.kwabenaberko.converter.toSignificantDigits
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RealConvertMoney(
    private val getRate: GetRate,
    private val updateDefaultCurrencies: UpdateDefaultCurrencies
) : ConvertMoney {
    override fun invoke(money: Money, targetCurrency: Currency): Flow<Money> {
        val (baseCurrency, amount) = money
        val baseCode = baseCurrency.code
        val targetCode = targetCurrency.code

        return getRate(baseCode, targetCode)
            .map { rate ->
                val convertedAmount = rate.times(amount)
                val roundedAmount = if (convertedAmount < 1) {
                    convertedAmount.toSignificantDigits(digits = 2)
                } else {
                    convertedAmount.toPlaces(places = 2)
                }
                updateDefaultCurrencies(baseCode, targetCode)
                Money(targetCurrency, roundedAmount)
            }
    }
}
