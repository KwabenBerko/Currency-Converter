package com.kwabenaberko.currencyconverter.android.converter.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.kwabenaberko.converter.domain.model.Currency
import com.kwabenaberko.currencyconverter.android.converter.ConverterViewModel.State
import com.kwabenaberko.currencyconverter.android.theme.CurrencyConverterTheme
import com.kwabenaberko.currencyconverter.android.theme.RedColorScheme

@Composable
internal fun ConverterScreenContent(
    state: State,
    onFirstCurrencyClick: (Currency) -> Unit = {},
    onFirstAmountClick: () -> Unit = {},
    onSecondCurrencyClick: (Currency) -> Unit = {},
    onSecondAmountClick: () -> Unit = {},
    onSyncRequired: () -> Unit = {}
) {

    val systemUiController = rememberSystemUiController()
    LaunchedEffect(systemUiController) {
        systemUiController.setStatusBarColor(color = RedColorScheme.primary)
    }

    LaunchedEffect(state) {
        if (state is State.RequiresSync) {
            onSyncRequired()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (state) {
            is State.Idle -> Unit
            is State.RequiresSync -> Unit
            is State.Content -> {
                Content(
                    state = state,
                    onFirstCurrencyClick = onFirstCurrencyClick,
                    onFirstAmountClick = onFirstAmountClick,
                    onSecondCurrencyClick = onSecondCurrencyClick,
                    onSecondAmountClick = onSecondAmountClick
                )
            }
        }
    }
}

@Composable
private fun Content(
    state: State.Content,
    onFirstCurrencyClick: (Currency) -> Unit,
    onFirstAmountClick: () -> Unit,
    onSecondCurrencyClick: (Currency) -> Unit,
    onSecondAmountClick: () -> Unit,
) {
    val firstMoneyItem = state.firstMoneyItem
    val secondMoneyItem = state.secondMoneyItem

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            CurrencyConverterTheme(useRedTheme = true) {
                ConverterPane(
                    modifier = Modifier.weight(1f)
                        .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 72.dp)
                ) {
                    val currency = firstMoneyItem.money.currency
                    val formattedAmount = firstMoneyItem.formattedAmount

                    CurrencyName(
                        name = currency.name,
                        onClick = { onFirstCurrencyClick(currency) }
                    )
                    CurrencyAmount(
                        formattedAmount = formattedAmount,
                        symbol = currency.symbol,
                        onClick = onFirstAmountClick
                    )
                    CurrencyCode(currency.code)
                }
            }

            CurrencyConverterTheme(useRedTheme = false) {
                ConverterPane(
                    modifier = Modifier.weight(1f)
                        .padding(start = 16.dp, top = 72.dp, end = 16.dp, bottom = 16.dp)
                ) {
                    val currency = secondMoneyItem.money.currency
                    val formattedAmount = secondMoneyItem.formattedAmount

                    CurrencyCode(currency.code)
                    CurrencyAmount(
                        formattedAmount = formattedAmount,
                        symbol = currency.symbol,
                        onClick = onSecondAmountClick
                    )
                    CurrencyName(
                        name = currency.name,
                        onClick = { onSecondCurrencyClick(currency) }
                    )
                }
            }
        }

        ConversionDirection(
            mode = state.conversionMode,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Preview
@Composable
private fun ConverterScreenContentPreview() {
    ConverterScreenContent(state = State.Idle)
}
