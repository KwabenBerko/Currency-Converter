package com.kwabenaberko.currencyconverter.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.navOptions
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.kwabenaberko.currencyconverter.android.converter.ConverterRoute
import com.kwabenaberko.currencyconverter.android.converter.converterGraph
import com.kwabenaberko.currencyconverter.android.converter.navigateToConverter
import com.kwabenaberko.currencyconverter.android.sync.SyncRoute
import com.kwabenaberko.currencyconverter.android.sync.navigateToSync
import com.kwabenaberko.currencyconverter.android.sync.syncScreen

@OptIn(ExperimentalAnimationApi::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
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
                            popUpTo(SyncRoute) {
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
