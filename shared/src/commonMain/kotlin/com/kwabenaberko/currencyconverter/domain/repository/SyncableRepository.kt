package com.kwabenaberko.currencyconverter.domain.repository

import com.kwabenaberko.currencyconverter.domain.model.SyncStatus
import kotlinx.coroutines.flow.Flow

interface SyncableRepository {
    fun syncStatus(): Flow<SyncStatus?>
    suspend fun hasCompletedInitialSync(): Boolean
    suspend fun sync(): Boolean
}