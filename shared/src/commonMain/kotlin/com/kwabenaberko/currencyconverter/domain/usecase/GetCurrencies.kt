package com.kwabenaberko.currencyconverter.domain.usecase

import com.kwabenaberko.currencyconverter.domain.model.Currency
import com.kwabenaberko.currencyconverter.domain.model.CurrencyFilter
import kotlinx.coroutines.flow.Flow

typealias GetCurrencies = (filter: CurrencyFilter?) -> Flow<List<Currency>>
