package com.kwabenaberko.currencyconverter.android

import android.app.Application
import com.kwabenaberko.converter.factory.Container
import com.kwabenaberko.converter.factory.ContainerFactory

class App : Application() {
    lateinit var container: Container

    override fun onCreate() {
        super.onCreate()
        container = ContainerFactory(context = this).makeContainer()
    }
}
