package com.kwabenaberko.currencyconverter.android

import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Density
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.kwabenaberko.converter.factory.Container
import com.kwabenaberko.converter.factory.ContainerFactory
import com.kwabenaberko.currencyconverter.android.converter.model.ConversionMode

private var container: Container? = null
fun Container.Companion.instance(context: Context): Container {
    if (container == null) {
        container = ContainerFactory(context).makeContainer()
    }
    return container as Container
}

fun Density.isAtMostXhdpi(): Boolean {
    return this.density <= 2.0
}

fun Configuration.isAtMostMediumHeight(): Boolean {
    return this.screenHeightDp <= 900
}

fun useRedTheme(conversionMode: ConversionMode): Boolean {
    return conversionMode == ConversionMode.FIRST_TO_SECOND
}

@Composable
fun NavBackStackEntry.rememberParentEntry(navController: NavController): NavBackStackEntry {
    val parentId = destination.parent!!.id
    return remember(this) { navController.getBackStackEntry(parentId) }
}
