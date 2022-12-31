package com.kwabenaberko.currencyconverter.android

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.DelegatingWorkerFactory
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.kwabenaberko.converter.factory.Container
import com.kwabenaberko.converter.factory.ContainerFactory
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.concurrent.TimeUnit

class App : Application(), Configuration.Provider {
    lateinit var container: Container
    private val mainScope = MainScope()

    override fun onCreate() {
        super.onCreate()
        container = ContainerFactory(context = this).makeContainer()
        container.hasCompletedInitialSync()
            .onEach { hasCompleted ->
                if (hasCompleted) {
                    schedulePeriodicSync()
                }
            }.launchIn(mainScope)
    }

    override fun getWorkManagerConfiguration(): Configuration {
        val workerFactory = DelegatingWorkerFactory().apply {
            addFactory(SyncWorker.Factory(container.sync))
        }
        return Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setWorkerFactory(workerFactory)
            .build()
    }

    private fun schedulePeriodicSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(3, TimeUnit.HOURS)
            .setInitialDelay(3, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager
            .getInstance(this)
            .enqueueUniquePeriodicWork(
                SyncWorker.TAG,
                ExistingPeriodicWorkPolicy.KEEP,
                syncRequest
            )
    }
}
