package com.kwabenaberko.converter.domain.usecase

import com.kwabenaberko.converter.domain.model.Currency
import kotlinx.coroutines.flow.Flow

fun interface GetCurrencies {
    operator fun invoke(filter: String?): Flow<List<Currency>>
}
