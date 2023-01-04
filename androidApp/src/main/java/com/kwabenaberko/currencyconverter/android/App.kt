package com.kwabenaberko.currencyconverter.android

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import androidx.work.DelegatingWorkerFactory
import com.kwabenaberko.converter.factory.Container
import com.kwabenaberko.converter.factory.ContainerFactory
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class App : Application(), Configuration.Provider {
    lateinit var container: Container
    private val mainScope = MainScope()

    override fun onCreate() {
        super.onCreate()
        container = ContainerFactory(context = this).makeContainer()
        container.hasCompletedInitialSync()
            .onEach { hasCompleted ->
                if (hasCompleted) {
                    SyncWorker.enqueue(this)
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
