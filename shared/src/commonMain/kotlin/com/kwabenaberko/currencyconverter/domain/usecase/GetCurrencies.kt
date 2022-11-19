package com.kwabenaberko.currencyconverter.domain.usecase

import com.kwabenaberko.currencyconverter.domain.model.Currency
import kotlinx.coroutines.flow.Flow

fun interface GetCurrencies {
    operator fun invoke(filter: String?): Flow<List<Currency>>
}
