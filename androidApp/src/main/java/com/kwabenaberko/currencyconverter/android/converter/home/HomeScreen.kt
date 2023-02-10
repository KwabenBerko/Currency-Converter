package com.kwabenaberko.currencyconverter.android.converter.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.kwabenaberko.converter.domain.model.Currency
import com.kwabenaberko.converter.presentation.viewmodel.ConverterViewModel
import com.kwabenaberko.converter.presentation.viewmodel.ConverterViewModel.State
import com.kwabenaberko.currencyconverter.android.converter.home.components.ConversionDirection
import com.kwabenaberko.currencyconverter.android.converter.home.components.ConverterPane
import com.kwabenaberko.currencyconverter.android.converter.home.components.CurrencyAmount
import com.kwabenaberko.currencyconverter.android.converter.home.components.CurrencyCode
import com.kwabenaberko.currencyconverter.android.converter.home.components.CurrencyName
import com.kwabenaberko.currencyconverter.android.isAtMostMediumHeight
import com.kwabenaberko.currencyconverter.android.isAtMostXhdpi
import com.kwabenaberko.currencyconverter.android.theme.CurrencyConverterTheme
import com.kwabenaberko.currencyconverter.android.theme.RedColorScheme

@Composable
internal fun HomeScreen(
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
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val shouldAdjustSize = density.isAtMostXhdpi() && configuration.isAtMostMediumHeight()

    val firstMoneyItem = state.firstMoneyItem
    val secondMoneyItem = state.secondMoneyItem

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            CurrencyConverterTheme(useRedTheme = true) {
                ConverterPane(modifier = Modifier.weight(1f)) {
                    val currency = firstMoneyItem.money.currency
                    val formattedAmount = firstMoneyItem.formattedAmount

                    CurrencyName(
                        name = currency.name,
                        fontSize = if (shouldAdjustSize) 22.sp else 24.sp,
                        onClick = { onFirstCurrencyClick(currency) }
                    )
                    CurrencyAmount(
                        amount = formattedAmount,
                        amountFontSize = if (shouldAdjustSize) 78.sp else 88.sp,
                        symbol = currency.symbol,
                        symbolFontSize = if (shouldAdjustSize) 20.sp else 24.sp,
                        onClick = onFirstAmountClick
                    )
                    CurrencyCode(
                        code = currency.code,
                        modifier = Modifier.offset(y = (-36).dp)
                    )
                }
            }

            CurrencyConverterTheme(useRedTheme = false) {
                ConverterPane(modifier = Modifier.weight(1f)) {
                    val currency = secondMoneyItem.money.currency
                    val formattedAmount = secondMoneyItem.formattedAmount

                    CurrencyCode(
                        code = currency.code,
                        modifier = Modifier.offset(y = 36.dp)
                    )
                    CurrencyAmount(
                        amount = formattedAmount,
                        amountFontSize = if (shouldAdjustSize) 78.sp else 88.sp,
                        symbol = currency.symbol,
                        symbolFontSize = if (shouldAdjustSize) 20.sp else 24.sp,
                        onClick = onSecondAmountClick
                    )
                    CurrencyName(
                        name = currency.name,
                        fontSize = if (shouldAdjustSize) 22.sp else 24.sp,
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
private fun HomeScreenContentPreview() {
    HomeScreen(state = ConverterViewModel.mockContentState)
}
