package com.kwabenaberko.currencyconverter.testdouble

import com.kwabenaberko.currencyconverter.domain.usecase.SetDefaultCurrencies

class SetDefaultCurrenciesSpy : SetDefaultCurrencies {
    var invocations: MutableList<Pair<String, String>> = mutableListOf()
        private set

    override suspend fun invoke(baseCode: String, targetCode: String) {
        invocations.add(Pair(baseCode, targetCode))
    }
}
