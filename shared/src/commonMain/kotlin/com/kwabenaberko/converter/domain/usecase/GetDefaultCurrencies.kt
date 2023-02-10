package com.kwabenaberko.converter.domain.usecase

import com.kwabenaberko.converter.domain.model.DefaultCurrencies
import kotlinx.coroutines.flow.Flow

fun interface GetDefaultCurrencies {
    operator fun invoke(): Flow<DefaultCurrencies>
}
