package com.kwabenaberko.currencyconverter.android.currencies

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kwabenaberko.currencyconverter.android.LocalContainer
import com.kwabenaberko.currencyconverter.android.converter.model.ConversionMode
import com.kwabenaberko.currencyconverter.android.converter.model.CurrenciesResult
import com.kwabenaberko.currencyconverter.android.currencies.components.CurrenciesScreenContent
import com.kwabenaberko.currencyconverter.android.useRedTheme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator

@Destination
@Composable
fun CurrenciesScreen(
    conversionMode: ConversionMode,
    navigator: DestinationsNavigator,
    resultNavigator: ResultBackNavigator<CurrenciesResult>,
    viewModel: CurrenciesViewModel = currenciesViewModel(),
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
private fun currenciesViewModel(): CurrenciesViewModel {
    val container = LocalContainer.current
    val factory = CurrenciesViewModel.Factory(getCurrencies = container.getCurrencies)
    return viewModel(factory = factory)
}
