package com.kwabenaberko.currencyconverter.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import com.kwabenaberko.currencyconverter.factory.Container
import com.ramcosta.composedestinations.DestinationsNavHost

val LocalContainer = staticCompositionLocalOf<Container> { error("No container found!") }

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = (applicationContext as App).container

        setContent {
            CompositionLocalProvider(LocalContainer provides container) {
                DestinationsNavHost(NavGraphs.root)
            }
        }
    }
}
