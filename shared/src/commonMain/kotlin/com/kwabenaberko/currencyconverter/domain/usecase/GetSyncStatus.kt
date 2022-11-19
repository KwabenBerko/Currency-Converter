package com.kwabenaberko.currencyconverter.domain.usecase

import com.kwabenaberko.currencyconverter.domain.model.SyncStatus
import kotlinx.coroutines.flow.Flow

fun interface GetSyncStatus {
    operator fun invoke(): Flow<SyncStatus?>
}
