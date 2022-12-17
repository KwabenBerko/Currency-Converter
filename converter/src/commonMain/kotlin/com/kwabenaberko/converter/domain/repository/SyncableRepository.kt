package com.kwabenaberko.converter.domain.repository

import com.kwabenaberko.converter.domain.model.SyncStatus
import kotlinx.coroutines.flow.Flow

interface SyncableRepository {
    fun getSyncStatus(): Flow<SyncStatus?>
    fun hasCompletedInitialSync(): Flow<Boolean>
    suspend fun sync(): Boolean
}