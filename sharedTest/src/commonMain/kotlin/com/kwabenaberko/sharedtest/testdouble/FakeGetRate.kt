package com.kwabenaberko.sharedtest.testdouble

import com.kwabenaberko.currencyconverter.domain.usecase.GetRate

class FakeGetRate : GetRate {
    var result: Double = 0.0

    override suspend fun invoke(baseCode: String, targetCode: String): Double {
        return result
    }
}
