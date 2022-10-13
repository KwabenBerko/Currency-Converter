package com.kwabenaberko.sharedtest.testdouble

import com.kwabenaberko.currencyconverter.domain.model.SyncStatus
import com.kwabenaberko.currencyconverter.domain.usecase.GetSyncStatus
import kotlinx.coroutines.flow.Flow

class FakeGetSyncStatus : GetSyncStatus {
    lateinit var result: Flow<SyncStatus?>

    override fun invoke(): Flow<SyncStatus?> {
        return result
    }
}
