package com.kwabenaberko.converter.domain.usecase

import kotlinx.coroutines.flow.Flow

fun interface GetRate {
    operator fun invoke(baseCode: String, targetCode: String): Flow<Double>
}
