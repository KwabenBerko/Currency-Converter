package com.kwabenaberko.convertertest.testdouble

import com.kwabenaberko.converter.domain.usecase.HasCompletedInitialSync
import kotlinx.coroutines.flow.Flow

class FakeHasCompletedInitialSync : HasCompletedInitialSync {
    lateinit var result: Flow<Boolean>

    override suspend fun invoke(): Flow<Boolean> {
        return result
    }
}
