package com.kwabenaberko.convertertest.testdouble

import com.kwabenaberko.converter.domain.usecase.GetRate
import kotlinx.coroutines.flow.Flow

class FakeGetRate : GetRate {
    lateinit var result: Flow<Double>

    override suspend fun invoke(baseCode: String, targetCode: String): Flow<Double> {
        return result
    }
}
