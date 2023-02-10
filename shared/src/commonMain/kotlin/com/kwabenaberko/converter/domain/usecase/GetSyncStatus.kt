package com.kwabenaberko.converter.domain.usecase

import com.kwabenaberko.converter.domain.model.SyncStatus
import kotlinx.coroutines.flow.Flow

fun interface GetSyncStatus {
    operator fun invoke(): Flow<SyncStatus?>
}
