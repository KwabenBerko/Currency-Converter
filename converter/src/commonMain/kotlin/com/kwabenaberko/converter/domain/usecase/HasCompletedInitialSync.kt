package com.kwabenaberko.converter.domain.usecase

import kotlinx.coroutines.flow.Flow

fun interface HasCompletedInitialSync {
    operator fun invoke(): Flow<Boolean>
}
