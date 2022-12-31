package com.kwabenaberko.currencyconverter.android.converter

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kwabenaberko.currencyconverter.android.LocalContainer
import com.kwabenaberko.currencyconverter.android.converter.ConverterViewModel.Factory
import com.kwabenaberko.currencyconverter.android.converter.animation.ConverterScreenTransitions
import com.kwabenaberko.currencyconverter.android.converter.components.ConverterScreenContent
import com.kwabenaberko.currencyconverter.android.converter.model.ConversionMode
import com.kwabenaberko.currencyconverter.android.converter.model.CurrenciesResult
import com.kwabenaberko.currencyconverter.android.converter.model.KeyPadResult
import com.kwabenaberko.currencyconverter.android.destinations.ConverterScreenDestination
import com.kwabenaberko.currencyconverter.android.destinations.CurrenciesScreenDestination
import com.kwabenaberko.currencyconverter.android.destinations.KeyPadScreenDestination
import com.kwabenaberko.currencyconverter.android.destinations.SyncScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.popUpTo
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient

@RootNavGraph(start = true)
@Destination(style = ConverterScreenTransitions::class)
@Composable
fun AnimatedVisibilityScope.ConverterScreen(
    navigator: DestinationsNavigator,
    currenciesResultRecipient: ResultRecipient<CurrenciesScreenDestination, CurrenciesResult>,
    keyPadResultRecipient: ResultRecipient<KeyPadScreenDestination, KeyPadResult>,
    viewModel: ConverterViewModel = converterViewModel()
) {
    val state by viewModel.state.collectAsState()

    currenciesResultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> Unit
            is NavResult.Value -> {
                val (conversionMode, currency) = result.value
                when (conversionMode) {
                    ConversionMode.FIRST_TO_SECOND -> {
                        viewModel.convertFirstMoney(currency)
                    }
                    ConversionMode.SECOND_TO_FIRST -> {
                        viewModel.convertSecondMoney(currency)
                    }
                }
            }
        }
    }

    keyPadResultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> Unit
            is NavResult.Value -> {
                val (conversionMode, amount) = result.value
                when (conversionMode) {
                    ConversionMode.FIRST_TO_SECOND -> {
                        viewModel.convertFirstMoney(amount)
                    }
                    ConversionMode.SECOND_TO_FIRST -> {
                        viewModel.convertSecondMoney(amount)
                    }
                }
            }
        }
    }

    ConverterScreenContent(
        state = state,
        onFirstCurrencyClick = { currency ->
            navigator.navigate(
                CurrenciesScreenDestination(
                    conversionMode = ConversionMode.FIRST_TO_SECOND,
                    selectedCurrencyCode = currency.code
                )
            )
        },
        onFirstAmountClick = {
            navigator.navigate(
                KeyPadScreenDestination(ConversionMode.FIRST_TO_SECOND)
            )
        },
        onSecondCurrencyClick = { currency ->
            navigator.navigate(
                CurrenciesScreenDestination(
                    conversionMode = ConversionMode.SECOND_TO_FIRST,
                    selectedCurrencyCode = currency.code
                )
            )
        },
        onSecondAmountClick = {
            navigator.navigate(
                KeyPadScreenDestination(ConversionMode.SECOND_TO_FIRST)
            )
        },
        onSyncRequired = {
            navigator.navigate(SyncScreenDestination) {
                popUpTo(ConverterScreenDestination) {
                    inclusive = true
                }
            }
        }
    )
}

@Composable
private fun converterViewModel(): ConverterViewModel {
    return with(LocalContainer.current) {
        val factory = Factory(
            hasCompletedInitialSync = this.hasCompletedInitialSync,
            getDefaultCurrencies = this.getDefaultCurrencies,
            convertMoney = this.convertMoney
        )
        viewModel(factory = factory)
    }
}
