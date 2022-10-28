package com.kwabenaberko.currencyconverter.android.converter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kwabenaberko.currencyconverter.android.LocalContainer
import com.kwabenaberko.currencyconverter.android.converter.ConverterViewModel.Factory
import com.kwabenaberko.currencyconverter.android.converter.ConverterViewModel.State
import com.kwabenaberko.currencyconverter.android.converter.components.ConverterContent
import com.kwabenaberko.currencyconverter.android.converter.model.ConversionMode
import com.kwabenaberko.currencyconverter.android.converter.model.CurrenciesResult
import com.kwabenaberko.currencyconverter.android.converter.model.KeyPadResult
import com.kwabenaberko.currencyconverter.android.destinations.CurrenciesScreenDestination
import com.kwabenaberko.currencyconverter.android.destinations.KeyPadScreenDestination
import com.kwabenaberko.currencyconverter.android.runIf
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient

@Destination
@Composable
fun ConverterScreen(
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
                state.runIf<State.Content> { currentState ->
                    val (conversionMode, currency) = result.value
                    val firstMoney = currentState.firstMoneyItem.money
                    val secondMoney = currentState.secondMoneyItem.money
                    when (conversionMode) {
                        ConversionMode.FIRST_MONEY_TO_SECOND_MONEY -> {
                            viewModel.convertFirstMoney(firstMoney.copy(currency = currency))
                        }
                        ConversionMode.SECOND_MONEY_TO_FIRST_MONEY -> {
                            viewModel.convertSecondMoney(secondMoney.copy(currency = currency))
                        }
                    }
                }
            }
        }
    }

    keyPadResultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> Unit
            is NavResult.Value -> {
                state.runIf<State.Content> { currentState ->
                    val (conversionMode, amount) = result.value
                    val firstMoney = currentState.firstMoneyItem.money
                    val secondMoney = currentState.secondMoneyItem.money
                    when (conversionMode) {
                        ConversionMode.FIRST_MONEY_TO_SECOND_MONEY -> {
                            viewModel.convertFirstMoney(firstMoney.copy(amount = amount))
                        }
                        ConversionMode.SECOND_MONEY_TO_FIRST_MONEY -> {
                            viewModel.convertSecondMoney(secondMoney.copy(amount = amount))
                        }
                    }
                }
            }
        }
    }

    ConverterContent(
        state = state,
        onFirstCurrencyClick = { currency ->
            navigator.navigate(
                CurrenciesScreenDestination(ConversionMode.FIRST_MONEY_TO_SECOND_MONEY)
            )
        },
        onFirstAmountClick = {
            navigator.navigate(
                KeyPadScreenDestination(ConversionMode.FIRST_MONEY_TO_SECOND_MONEY)
            )
        },
        onSecondCurrencyClick = { currency ->
            navigator.navigate(
                CurrenciesScreenDestination(ConversionMode.SECOND_MONEY_TO_FIRST_MONEY)
            )
        },
        onSecondAmountClick = {
            navigator.navigate(
                KeyPadScreenDestination(ConversionMode.SECOND_MONEY_TO_FIRST_MONEY)
            )
        }
    )
}

@Composable
private fun converterViewModel(): ConverterViewModel {
    return with(LocalContainer.current) {
        val factory = Factory(
            getDefaultCurrencies = this.getDefaultCurrencies,
            convertMoney = this.convertMoney
        )
        viewModel(factory = factory)
    }
}
