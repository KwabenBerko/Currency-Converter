package com.kwabenaberko.currencyconverter.domain.usecase

import com.kwabenaberko.currencyconverter.domain.model.DefaultCurrencies
import kotlinx.coroutines.flow.Flow

typealias GetDefaultCurrencies = () -> Flow<DefaultCurrencies>
