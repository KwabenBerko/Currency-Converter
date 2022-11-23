package com.kwabenaberko.convertertest.testdouble

import com.kwabenaberko.converter.domain.usecase.GetRate

class FakeGetRate : GetRate {
    var result: Double = 0.0

    override suspend fun invoke(baseCode: String, targetCode: String): Double {
        return result
    }
}
