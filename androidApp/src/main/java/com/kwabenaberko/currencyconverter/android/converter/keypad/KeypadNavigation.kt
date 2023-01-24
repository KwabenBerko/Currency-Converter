package com.kwabenaberko.currencyconverter.android.converter.keypad

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.composable
import com.kwabenaberko.converter.presentation.model.ConversionMode
import com.kwabenaberko.converter.presentation.viewmodel.ConverterViewModel
import com.kwabenaberko.converter.presentation.viewmodel.KeypadViewModel
import com.kwabenaberko.currencyconverter.android.useRedTheme

internal const val KeyPadRoute = "keypad?mode={mode}"

internal fun NavController.navigateToKeyPad(conversionMode: ConversionMode) {
    navigate("keypad?mode=${conversionMode.name}")
}

@OptIn(ExperimentalAnimationApi::class)
internal fun NavGraphBuilder.keypadScreen(
    getConverterViewModel: @Composable (backStackEntry: NavBackStackEntry) -> ConverterViewModel,
    onNavigateBack: () -> Unit
) {
    composable(
        route = KeyPadRoute,
        arguments = listOf(
            navArgument("mode") { type = NavType.StringType }
        ),
        enterTransition = {
            slideInVertically(
                initialOffsetY = { fullHeight -> fullHeight }
            )
        },
        exitTransition = {
            slideOutVertically(
                targetOffsetY = { fullHeight -> fullHeight },
            )
        }
    ) { backStackEntry ->
        val conversionMode = ConversionMode.valueOf(
            backStackEntry.arguments?.getString("mode")!!
        )
        val converterViewModel = getConverterViewModel(backStackEntry)
        val viewModel = keypadViewModel(backStackEntry)
        val state by viewModel.state.collectAsState()

        KeyPadScreen(
            useRedTheme = useRedTheme(conversionMode),
            state = state,
            onBackClick = onNavigateBack,
            onAppend = viewModel::add,
            onUndo = viewModel::pop,
            onDone = { amount ->
                when (conversionMode) {
                    ConversionMode.FIRST_TO_SECOND -> {
                        converterViewModel.convertFirstMoney(amount)
                    }
                    ConversionMode.SECOND_TO_FIRST -> {
                        converterViewModel.convertSecondMoney(amount)
                    }
                }
                onNavigateBack()
            }
        )
    }
}

@Composable
fun keypadViewModel(owner: ViewModelStoreOwner): KeypadViewModel {
    return viewModel(owner)
}
