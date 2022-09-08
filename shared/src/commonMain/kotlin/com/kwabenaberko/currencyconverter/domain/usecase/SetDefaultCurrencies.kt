package com.kwabenaberko.currencyconverter.domain.usecase

typealias SetDefaultCurrencies = suspend (
    baseCode: String,
    targetCode: String
) -> Unit
