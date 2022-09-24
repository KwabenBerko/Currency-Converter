package com.kwabenaberko.sharedtest.testdouble

import com.kwabenaberko.currencyconverter.domain.usecase.Sync

class FakeSync : Sync {
    var result: Boolean = false

    override suspend fun invoke(): Boolean {
        return result
    }
}
