package com.kwabenaberko.converter.testdouble

import com.kwabenaberko.converter.domain.usecase.HasCompletedInitialSync
import kotlinx.coroutines.flow.Flow

class FakeHasCompletedInitialSync : HasCompletedInitialSync {
    lateinit var result: Flow<Boolean>

    override fun invoke(): Flow<Boolean> {
        return result
    }
}
