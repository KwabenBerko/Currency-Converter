package com.kwabenaberko.convertertest.testdouble

import com.kwabenaberko.converter.domain.usecase.HasCompletedInitialSync

class FakeHasCompletedInitialSync : HasCompletedInitialSync {
    var result: Boolean = false

    override suspend fun invoke(): Boolean {
        return result
    }
}
