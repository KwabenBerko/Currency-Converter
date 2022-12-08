package com.kwabenaberko.currencyconverter.android.currencies

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kwabenaberko.currencyconverter.android.LocalContainer
import com.kwabenaberko.currencyconverter.android.converter.model.ConversionMode
import com.kwabenaberko.currencyconverter.android.converter.model.CurrenciesResult
import com.kwabenaberko.currencyconverter.android.currencies.animation.CurrenciesScreenTransitions
import com.kwabenaberko.currencyconverter.android.currencies.components.CurrenciesScreenContent
import com.kwabenaberko.currencyconverter.android.useRedTheme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator

@Destination(style = CurrenciesScreenTransitions::class)
@Composable
fun AnimatedVisibilityScope.CurrenciesScreen(
    conversionMode: ConversionMode,
    selectedCurrencyCode: String,
    navigator: DestinationsNavigator,
    resultNavigator: ResultBackNavigator<CurrenciesResult>,
    viewModel: CurrenciesViewModel = currenciesViewModel(selectedCurrencyCode),
) {

    val state by viewModel.state.collectAsState()

    CurrenciesScreenContent(
        useRedTheme = useRedTheme(conversionMode),
        state = state,
        onBackClick = {
            navigator.popBackStack()
        },
        onFilterQueryChange = viewModel::filterCurrencies,
        onCurrencyClick = { currency ->
            resultNavigator.navigateBack(CurrenciesResult(conversionMode, currency))
        }
    )
}

@Composable
private fun currenciesViewModel(selectedCurrencyCode: String): CurrenciesViewModel {
    val container = LocalContainer.current
    val factory = CurrenciesViewModel.Factory(
        selectedCurrencyCode = selectedCurrencyCode,
        getCurrencies = container.getCurrencies
    )
    return viewModel(factory = factory)
}
