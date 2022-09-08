package com.kwabenaberko.currencyconverter.domain.usecase

typealias GetRate = suspend (
    baseCode: String,
    targetCode: String
) -> Double
