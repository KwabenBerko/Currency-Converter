package com.kwabenaberko.currencyconverter.android

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import androidx.work.DelegatingWorkerFactory
import com.kwabenaberko.converter.factory.Container
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class App : Application(), Configuration.Provider {
    private lateinit var container: Container
    private val mainScope = MainScope()

    override fun onCreate() {
        super.onCreate()
        container = Container.instance(this)
        container.hasCompletedInitialSync()
            .onEach { hasCompleted ->
                if (hasCompleted) {
                    SyncWorker.create(this)
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
}
