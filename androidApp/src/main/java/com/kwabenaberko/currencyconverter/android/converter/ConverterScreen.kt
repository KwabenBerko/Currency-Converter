package com.kwabenaberko.currencyconverter.android.converter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kwabenaberko.currencyconverter.android.LocalContainer
import com.kwabenaberko.currencyconverter.android.converter.components.ConverterScreenContent
import com.kwabenaberko.currencyconverter.android.converter.model.CurrenciesResult
import com.kwabenaberko.currencyconverter.android.converter.model.KeyPadResult
import com.kwabenaberko.currencyconverter.android.destinations.CurrenciesScreenDestination
import com.kwabenaberko.currencyconverter.android.destinations.KeyPadScreenDestination
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
                val currentState = state
                val currency = result.value.currency
                if (currentState is ConverterViewModel.State.Content) {
                    val firstMoney = currentState.firstMoneyItem.money
                    val secondMoney = currentState.secondMoneyItem.money
                    if (result.value.isReverse) {
                        viewModel.convertSecondMoney(secondMoney.copy(currency = currency))
                    } else {
                        viewModel.convertFirstMoney(firstMoney.copy(currency = currency))
                    }
                }
            }
        }
    }

    keyPadResultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> Unit
            is NavResult.Value -> {
                val currentState = state
                val amount = result.value.amount
                if (currentState is ConverterViewModel.State.Content) {
                    val firstMoney = currentState.firstMoneyItem.money
                    val secondMoney = currentState.secondMoneyItem.money
                    if (result.value.isReverse) {
                        viewModel.convertSecondMoney(secondMoney.copy(amount = amount))
                    } else {
                        viewModel.convertFirstMoney(firstMoney.copy(amount = amount))
                    }
                }
            }
        }
    }

    ConverterScreenContent(
        state = state,
        onFirstCurrencyClick = { currency ->
            navigator.navigate(CurrenciesScreenDestination(false))
        },
        onFirstAmountClick = {
            navigator.navigate(KeyPadScreenDestination(false))
        },
        onSecondCurrencyClick = { currency ->
            navigator.navigate(CurrenciesScreenDestination(true))
        },
        onSecondAmountClick = {
            navigator.navigate(KeyPadScreenDestination(true))
        }
    )
}

@Composable
private fun converterViewModel(): ConverterViewModel {
    return with(LocalContainer.current) {
        val factory = ConverterViewModel.Factory(
            getDefaultCurrencies = this.getDefaultCurrencies,
            convertMoney = this.convertMoney
        )
        viewModel(factory = factory)
    }
}
