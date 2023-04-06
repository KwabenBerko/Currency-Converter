package com.kwabenaberko.converter.domain.usecase

import com.kwabenaberko.converter.domain.model.Currency
import com.kwabenaberko.converter.domain.model.Money
import com.kwabenaberko.converter.toPlaces
import com.kwabenaberko.converter.toSignificantDigits
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun interface ConvertMoney {
    operator fun invoke(money: Money, targetCurrency: Currency): Flow<Money>
}
