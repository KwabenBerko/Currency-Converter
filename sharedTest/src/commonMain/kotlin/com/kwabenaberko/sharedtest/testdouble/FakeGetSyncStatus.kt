package com.kwabenaberko.sharedtest.testdouble

import com.kwabenaberko.currencyconverter.domain.model.SyncStatus
import com.kwabenaberko.currencyconverter.domain.usecase.GetSyncStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class FakeGetSyncStatus: GetSyncStatus {
    val result = MutableSharedFlow<SyncStatus>()

    override fun invoke(): Flow<SyncStatus> {
        return result
    }
}
