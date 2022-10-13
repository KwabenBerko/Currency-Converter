package com.kwabenaberko.currencyconverter.domain.usecase

import com.kwabenaberko.currencyconverter.domain.model.Currency
import kotlinx.coroutines.flow.Flow

typealias GetCurrencies = (filter: String?) -> Flow<List<Currency>>
