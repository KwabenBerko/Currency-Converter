package com.kwabenaberko.currencyconverter.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.kwabenaberko.currencyconverter.factory.Container
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine

val LocalContainer = staticCompositionLocalOf<Container> {
    error("No container found!")
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialNavigationApi::class)
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = (applicationContext as App).container

        setContent {
            CompositionLocalProvider(LocalContainer provides container) {
                DestinationsNavHost(
                    navGraph = NavGraphs.root,
                    engine = rememberAnimatedNavHostEngine()
                )
            }
        }
    }
}
