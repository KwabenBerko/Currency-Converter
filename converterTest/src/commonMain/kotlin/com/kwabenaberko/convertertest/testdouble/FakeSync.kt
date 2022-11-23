package com.kwabenaberko.convertertest.testdouble

import com.kwabenaberko.converter.domain.usecase.Sync

class FakeSync : Sync {
    var result: Boolean = false

    override suspend fun invoke(): Boolean {
        return result
    }
}
