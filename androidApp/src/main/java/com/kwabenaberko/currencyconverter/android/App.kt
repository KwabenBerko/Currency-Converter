package com.kwabenaberko.currencyconverter.android

import android.app.Application
import com.kwabenaberko.currencyconverter.factory.Container
import com.kwabenaberko.currencyconverter.factory.ContainerFactory

class App : Application() {
    lateinit var container: Container

    override fun onCreate() {
        super.onCreate()
        container = ContainerFactory(context = this).makeContainer()
    }
}
