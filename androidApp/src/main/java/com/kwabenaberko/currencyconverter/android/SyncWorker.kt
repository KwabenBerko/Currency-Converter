package com.kwabenaberko.currencyconverter.android

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.kwabenaberko.converter.domain.usecase.Sync

class SyncWorker(
    appContext: Context,
    workerParameters: WorkerParameters,
    private val sync: Sync
) : CoroutineWorker(appContext, workerParameters) {

    override suspend fun doWork(): Result {
        return when (sync()) {
            true -> Result.success()
            false -> Result.failure()
        }
    }

    class Factory(private val sync: Sync) : WorkerFactory() {
        override fun createWorker(
            appContext: Context,
            workerClassName: String,
            workerParameters: WorkerParameters
        ): ListenableWorker {
            return SyncWorker(appContext, workerParameters, sync)
        }
    }

    companion object {
        const val TAG = "sync"
    }
}
