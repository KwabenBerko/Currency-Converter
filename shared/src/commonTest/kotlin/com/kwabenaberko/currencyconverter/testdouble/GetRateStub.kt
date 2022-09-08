package com.kwabenaberko.currencyconverter.testdouble

import com.kwabenaberko.currencyconverter.domain.usecase.GetRate

class GetRateStub : GetRate {
    var result: Double = 0.0

    override suspend fun invoke(baseCode: String, targetCode: String): Double {
        return result
    }
}
