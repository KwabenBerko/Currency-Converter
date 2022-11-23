package com.kwabenaberko.convertertest.testdouble

import com.kwabenaberko.converter.domain.usecase.SetDefaultCurrencies

class FakeSetDefaultCurrencies : SetDefaultCurrencies {
    var invocations: MutableList<Pair<String, String>> = mutableListOf()
        private set

    override suspend fun invoke(baseCode: String, targetCode: String) {
        invocations.add(Pair(baseCode, targetCode))
    }
}
