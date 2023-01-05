package com.kwabenaberko.currencyconverter.android

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.ListenableWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.kwabenaberko.converter.domain.usecase.Sync
import java.util.concurrent.TimeUnit

class SyncWorker(
    private val appContext: Context,
    workerParameters: WorkerParameters,
    private val sync: Sync
) : CoroutineWorker(appContext, workerParameters) {

    override suspend fun doWork(): Result {
        return when (sync()) {
            true -> {
                create(appContext, ExistingWorkPolicy.APPEND)
                Result.success()
            }
            false -> Result.retry()
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
        private const val TAG = "sync"

        fun create(
            context: Context,
            existingWorkPolicy: ExistingWorkPolicy = ExistingWorkPolicy.KEEP
        ) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val syncWork = OneTimeWorkRequestBuilder<SyncWorker>()
                .setInitialDelay(12, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(TAG, existingWorkPolicy, syncWork)
        }
    }
}
