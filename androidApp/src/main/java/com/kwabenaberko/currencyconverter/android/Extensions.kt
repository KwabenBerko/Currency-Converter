package com.kwabenaberko.currencyconverter.android

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.kwabenaberko.currencyconverter.android.converter.model.ConversionMode

fun useRedTheme(conversionMode: ConversionMode): Boolean {
    return conversionMode == ConversionMode.FIRST_TO_SECOND
}

@Composable
fun NavBackStackEntry.rememberParentEntry(navController: NavController): NavBackStackEntry {
    val parentId = destination.parent!!.id
    return remember(this) { navController.getBackStackEntry(parentId) }
}