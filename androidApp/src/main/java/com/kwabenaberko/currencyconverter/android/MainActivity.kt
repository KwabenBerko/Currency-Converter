package com.kwabenaberko.currencyconverter.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.navOptions
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.kwabenaberko.converter.factory.Container
import com.kwabenaberko.currencyconverter.android.converter.ConverterRoute
import com.kwabenaberko.currencyconverter.android.converter.converterGraph
import com.kwabenaberko.currencyconverter.android.converter.navigateToConverter
import com.kwabenaberko.currencyconverter.android.sync.SyncRoute
import com.kwabenaberko.currencyconverter.android.sync.navigateToSync
import com.kwabenaberko.currencyconverter.android.sync.syncScreen

val LocalContainer = staticCompositionLocalOf<Container> {
    error("No container found!")
}

@OptIn(ExperimentalAnimationApi::class)
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = (applicationContext as App).container

        setContent {
            CompositionLocalProvider(LocalContainer provides container) {
                val navController = rememberAnimatedNavController()
                AnimatedNavHost(
                    navController = navController,
                    startDestination = ConverterRoute
                ) {
                    converterGraph(
                        navController = navController,
                        onNavigateToSync = {
                            val navOptions = navOptions {
                                popUpTo(ConverterRoute) {
                                    inclusive = true
                                }
                            }
                            navController.navigateToSync(navOptions)
                        }
                    )

                    syncScreen(
                        onNavigateToConverter = {
                            val navOptions = navOptions {
                                popUpTo(SyncRoute){
                                    inclusive = true
                                }
                            }
                            navController.navigateToConverter(navOptions)
                        }
                    )
                }
            }
        }
    }
}
