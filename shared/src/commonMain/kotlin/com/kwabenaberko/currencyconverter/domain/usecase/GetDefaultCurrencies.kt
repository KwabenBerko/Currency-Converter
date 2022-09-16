package com.kwabenaberko.currencyconverter.domain.usecase

import com.kwabenaberko.currencyconverter.domain.model.DefaultCurrencies

typealias GetDefaultCurrencies = suspend () -> DefaultCurrencies
