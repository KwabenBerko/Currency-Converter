package com.kwabenaberko.converter.testdouble

import com.kwabenaberko.converter.domain.model.SyncStatus
import com.kwabenaberko.converter.domain.usecase.GetSyncStatus
import kotlinx.coroutines.flow.Flow

class FakeGetSyncStatus : GetSyncStatus {
    lateinit var result: Flow<SyncStatus?>

    override fun invoke(): Flow<SyncStatus?> {
        return result
    }
}
