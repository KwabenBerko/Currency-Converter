package com.kwabenaberko.currencyconverter.android.converter.currencies

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.composable
import com.kwabenaberko.converter.domain.model.Currency
import com.kwabenaberko.converter.factory.Container
import com.kwabenaberko.currencyconverter.android.converter.ConverterViewModel
import com.kwabenaberko.currencyconverter.android.converter.model.ConversionMode
import com.kwabenaberko.currencyconverter.android.instance
import com.kwabenaberko.currencyconverter.android.useRedTheme

internal const val CurrenciesRoute = "currencies?mode={mode}&selected={selected}"

internal fun NavController.navigateToCurrencies(
    conversionMode: ConversionMode,
    selectedCurrency: Currency
) {
    val mode = conversionMode.name
    val selected = selectedCurrency.code
    navigate("currencies?mode=$mode&selected=$selected")
}

@OptIn(ExperimentalAnimationApi::class)
internal fun NavGraphBuilder.currenciesScreen(
    getConverterViewModel: @Composable (backStackEntry: NavBackStackEntry) -> ConverterViewModel,
    onNavigateBack: () -> Unit
) {
    composable(
        route = CurrenciesRoute,
        arguments = listOf(
            navArgument("mode") { type = NavType.StringType },
            navArgument("selected") { type = NavType.StringType }
        ),
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth }
            )
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth }
            )
        }
    ) { backStackEntry ->
        val conversionMode = ConversionMode.valueOf(
            backStackEntry.arguments?.getString("mode")!!
        )
        val selected = backStackEntry.arguments?.getString("selected")!!
        val converterViewModel = getConverterViewModel(backStackEntry)
        val viewModel = currenciesViewModel(
            owner = backStackEntry,
            selectedCurrencyCode = selected
        )
        val state by viewModel.state.collectAsState()

        CurrenciesScreen(
            useRedTheme = useRedTheme(conversionMode),
            state = state,
            onBackClick = onNavigateBack,
            onFilterQueryChange = viewModel::filterCurrencies,
            onCurrencyClick = { currency ->
                when (conversionMode) {
                    ConversionMode.FIRST_TO_SECOND -> {
                        converterViewModel.convertFirstMoney(currency)
                    }
                    ConversionMode.SECOND_TO_FIRST -> {
                        converterViewModel.convertSecondMoney(currency)
                    }
                }
                onNavigateBack()
            }
        )
    }
}

@Composable
fun currenciesViewModel(
    owner: ViewModelStoreOwner,
    selectedCurrencyCode: String
): CurrenciesViewModel {
    return with(Container.instance(LocalContext.current)) {
        val factory = CurrenciesViewModel.Factory(
            selectedCurrencyCode = selectedCurrencyCode,
            getCurrencies = this.getCurrencies
        )
        viewModel(viewModelStoreOwner = owner, factory = factory)
    }
}
