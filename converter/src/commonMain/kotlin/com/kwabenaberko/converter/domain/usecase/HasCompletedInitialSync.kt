package com.kwabenaberko.converter.domain.usecase

import kotlinx.coroutines.flow.Flow

@Suppress("FUN_INTERFACE_WITH_SUSPEND_FUNCTION")
fun interface HasCompletedInitialSync {
    suspend operator fun invoke(): Flow<Boolean>
}
