package com.kwabenaberko.converter.testdouble

import com.kwabenaberko.converter.domain.usecase.UpdateDefaultCurrencies

class FakeUpdateDefaultCurrencies : UpdateDefaultCurrencies {
    var invocations: MutableList<Pair<String, String>> = mutableListOf()
        private set

    override suspend fun invoke(baseCode: String, targetCode: String) {
        invocations.add(Pair(baseCode, targetCode))
    }
}
