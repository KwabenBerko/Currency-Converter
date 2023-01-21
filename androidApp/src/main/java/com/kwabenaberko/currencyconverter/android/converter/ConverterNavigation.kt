package com.kwabenaberko.currencyconverter.android.converter

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.navigation
import com.kwabenaberko.converter.factory.Container
import com.kwabenaberko.converter.presentation.viewmodel.ConverterViewModel
import com.kwabenaberko.currencyconverter.android.converter.currencies.currenciesScreen
import com.kwabenaberko.currencyconverter.android.converter.currencies.navigateToCurrencies
import com.kwabenaberko.currencyconverter.android.converter.home.HomeRoute
import com.kwabenaberko.currencyconverter.android.converter.home.homeScreen
import com.kwabenaberko.currencyconverter.android.converter.keypad.keypadScreen
import com.kwabenaberko.currencyconverter.android.converter.keypad.navigateToKeyPad
import com.kwabenaberko.currencyconverter.android.instance
import com.kwabenaberko.currencyconverter.android.rememberParentEntry

const val ConverterRoute = "converter"

fun NavController.navigateToConverter(navOptions: NavOptions?) {
    navigate(ConverterRoute, navOptions)
}

fun NavGraphBuilder.converterGraph(
    navController: NavController,
    onNavigateToSync: () -> Unit
) {
    navigation(
        startDestination = HomeRoute,
        route = ConverterRoute
    ) {
        homeScreen(
            getConverterViewModel = { backStackEntry ->
                converterViewModel(backStackEntry.rememberParentEntry(navController))
            },
            onNavigateToCurrencies = navController::navigateToCurrencies,
            onNavigateToKeyPad = navController::navigateToKeyPad,
            onNavigateToSync = onNavigateToSync
        )

        currenciesScreen(
            getConverterViewModel = { backStackEntry ->
                converterViewModel(backStackEntry.rememberParentEntry(navController))
            },
            onNavigateBack = {
                navController.popBackStack()
            }
        )

        keypadScreen(
            getConverterViewModel = { backStackEntry ->
                converterViewModel(backStackEntry.rememberParentEntry(navController))
            },
            onNavigateBack = {
                navController.popBackStack()
            }
        )
    }
}

@Composable
private fun converterViewModel(owner: ViewModelStoreOwner): ConverterViewModel {
    return with(Container.instance(LocalContext.current)) {
        val factory = ConverterViewModelFactory(
            hasCompletedInitialSync,
            getDefaultCurrencies,
            convertMoney
        )
        viewModel(viewModelStoreOwner = owner, factory = factory)
    }
}
