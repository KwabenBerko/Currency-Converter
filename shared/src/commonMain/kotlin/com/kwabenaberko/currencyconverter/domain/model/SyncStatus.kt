package com.kwabenaberko.currencyconverter.domain.model

sealed interface SyncStatus {
    object Idle : SyncStatus
    object InProgress : SyncStatus
    object Error : SyncStatus
}
