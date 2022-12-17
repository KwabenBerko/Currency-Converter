package com.kwabenaberko.converter.domain.usecase

import kotlinx.coroutines.flow.Flow

@Suppress("FUN_INTERFACE_WITH_SUSPEND_FUNCTION")
fun interface GetRate {
    suspend operator fun invoke(baseCode: String, targetCode: String): Flow<Double>
}
