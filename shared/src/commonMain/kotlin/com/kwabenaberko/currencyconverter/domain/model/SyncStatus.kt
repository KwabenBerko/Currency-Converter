package com.kwabenaberko.currencyconverter.domain.model

sealed class SyncStatus {
    object InProgress : SyncStatus()
    object Error : SyncStatus()
    object Success : SyncStatus()
}
