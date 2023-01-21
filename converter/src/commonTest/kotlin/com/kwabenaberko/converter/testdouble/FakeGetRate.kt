package com.kwabenaberko.converter.testdouble

import com.kwabenaberko.converter.domain.usecase.GetRate
import kotlinx.coroutines.flow.Flow

class FakeGetRate : GetRate {
    lateinit var result: Flow<Double>

    override fun invoke(baseCode: String, targetCode: String): Flow<Double> {
        return result
    }
}
