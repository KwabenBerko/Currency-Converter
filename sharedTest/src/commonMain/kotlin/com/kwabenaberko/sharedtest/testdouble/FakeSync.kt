package com.kwabenaberko.sharedtest.testdouble

import com.kwabenaberko.currencyconverter.domain.usecase.Sync

class FakeSync : Sync {
    var invocations: Int = 0
        private set

    override suspend fun invoke() {
        invocations++
    }
}
