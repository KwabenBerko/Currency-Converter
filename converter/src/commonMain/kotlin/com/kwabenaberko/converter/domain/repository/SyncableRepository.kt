package com.kwabenaberko.converter.domain.repository

import com.kwabenaberko.converter.domain.model.SyncStatus
import kotlinx.coroutines.flow.Flow

interface SyncableRepository {
    fun syncStatus(): Flow<SyncStatus?>
    suspend fun hasCompletedInitialSync(): Boolean
    suspend fun sync(): Boolean
}