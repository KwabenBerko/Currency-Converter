package com.kwabenaberko.sharedtest.testdouble

import com.kwabenaberko.currencyconverter.domain.usecase.HasCompletedInitialSync

class FakeHasCompletedInitialSync : HasCompletedInitialSync {
    var result: Boolean = false

    override suspend fun invoke(): Boolean {
        return result
    }
}
