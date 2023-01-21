package com.kwabenaberko.currencyconverter.android.converter.home

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import com.kwabenaberko.converter.domain.model.Currency
import com.kwabenaberko.converter.presentation.model.ConversionMode
import com.kwabenaberko.converter.presentation.viewmodel.ConverterViewModel

internal const val HomeRoute = "home"

@OptIn(ExperimentalAnimationApi::class)
internal fun NavGraphBuilder.homeScreen(
    getConverterViewModel: @Composable (backStackEntry: NavBackStackEntry) -> ConverterViewModel,
    onNavigateToCurrencies: (
        conversionMode: ConversionMode,
        selectedCurrency: Currency
    ) -> Unit,
    onNavigateToKeyPad: (conversionMode: ConversionMode) -> Unit,
    onNavigateToSync: () -> Unit
) {
    composable(
        route = HomeRoute,
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() }
    ) { backStackEntry ->

        val viewModel = getConverterViewModel(backStackEntry)
        val state by viewModel.state.collectAsState()

        HomeScreen(
            state = state,
            onFirstCurrencyClick = { currency ->
                onNavigateToCurrencies(ConversionMode.FIRST_TO_SECOND, currency)
            },
            onFirstAmountClick = {
                onNavigateToKeyPad(ConversionMode.FIRST_TO_SECOND)
            },
            onSecondCurrencyClick = { currency ->
                onNavigateToCurrencies(ConversionMode.SECOND_TO_FIRST, currency)
            },
            onSecondAmountClick = {
                onNavigateToKeyPad(ConversionMode.SECOND_TO_FIRST)
            },
            onSyncRequired = onNavigateToSync
        )
    }
}
