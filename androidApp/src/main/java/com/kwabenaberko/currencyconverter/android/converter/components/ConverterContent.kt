package com.kwabenaberko.currencyconverter.android.converter.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.kwabenaberko.currencyconverter.android.R
import com.kwabenaberko.currencyconverter.android.converter.ConverterViewModel.State
import com.kwabenaberko.currencyconverter.android.converter.model.ConversionMode
import com.kwabenaberko.currencyconverter.android.isLight
import com.kwabenaberko.currencyconverter.android.theme.CurrencyConverterTheme
import com.kwabenaberko.currencyconverter.android.theme.RedColorScheme
import com.kwabenaberko.currencyconverter.android.theme.WhiteColorScheme
import com.kwabenaberko.currencyconverter.domain.model.Currency

@Composable
fun ConverterContent(
    state: State,
    onFirstCurrencyClick: (Currency) -> Unit = {},
    onFirstAmountClick: () -> Unit = {},
    onSecondCurrencyClick: (Currency) -> Unit = {},
    onSecondAmountClick: () -> Unit = {}
) {

    val colorScheme = RedColorScheme.primary
    val systemUiController = rememberSystemUiController()

    LaunchedEffect(systemUiController) {
        systemUiController.setStatusBarColor(
            color = colorScheme,
            darkIcons = colorScheme.isLight()
        )
    }

    when (state) {
        is State.Idle -> Unit
        is State.Content -> {
            val firstMoneyItem = state.firstMoneyItem
            val secondMoneyItem = state.secondMoneyItem

            Box(modifier = Modifier.fillMaxSize()) {
                Column {
                    CurrencyConverterTheme(useRedTheme = true) {
                        val currency = firstMoneyItem.money.currency
                        val formattedAmount = firstMoneyItem.formattedAmount

                        Column(
                            modifier = Modifier.background(MaterialTheme.colorScheme.primary)
                                .fillMaxSize()
                                .weight(1f)
                                .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 72.dp),
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CurrencyName(
                                name = currency.name,
                                modifier = Modifier.clickable(
                                    onClick = { onFirstCurrencyClick(currency) }
                                )
                            )
                            AmountAndSymbol(
                                formattedAmount = formattedAmount,
                                symbol = currency.symbol,
                                modifier = Modifier.clickable(onClick = onFirstAmountClick)
                            )
                            CurrencyCode(currency.code)
                        }
                    }
                    CurrencyConverterTheme(useRedTheme = false) {
                        val currency = secondMoneyItem.money.currency
                        val formattedAmount = secondMoneyItem.formattedAmount

                        Column(
                            modifier = Modifier.background(MaterialTheme.colorScheme.primary)
                                .fillMaxSize()
                                .weight(1f)
                                .padding(start = 16.dp, top = 72.dp, end = 16.dp, bottom = 16.dp),
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CurrencyCode(currency.code)
                            AmountAndSymbol(
                                formattedAmount = formattedAmount,
                                symbol = currency.symbol,
                                modifier = Modifier.clickable(onClick = onSecondAmountClick)
                            )
                            CurrencyName(
                                name = currency.name,
                                modifier = Modifier.clickable(
                                    onClick = { onSecondCurrencyClick(currency) }
                                )
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
    }
}

@Composable
fun AmountAndSymbol(
    formattedAmount: String,
    symbol: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = buildAnnotatedString {
            withStyle(SpanStyle(color = MaterialTheme.colorScheme.onPrimary, fontSize = 72.sp)) {
                append(formattedAmount)
            }
            withStyle(SpanStyle(color = MaterialTheme.colorScheme.secondary, fontSize = 24.sp)) {
                append(symbol)
            }
        },
        modifier = modifier,
        style = MaterialTheme.typography.labelLarge,
    )
}

@Composable
fun CurrencyName(
    name: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = name,
        modifier = modifier,
        style = MaterialTheme.typography.labelLarge.copy(fontSize = 26.sp),
        color = MaterialTheme.colorScheme.onPrimary
    )
}

@Composable
fun CurrencyCode(
    code: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = code,
        modifier = modifier,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.secondary
    )
}

@Composable
fun ConversionDirection(
    mode: ConversionMode,
    modifier: Modifier = Modifier
) {
    Box(
        modifier.background(WhiteColorScheme.primary, CircleShape)
            .border(BorderStroke(6.dp, RedColorScheme.primary), CircleShape)
            .padding(20.dp)
    ) {
        Icon(
            painter = when (mode) {
                ConversionMode.FIRST_MONEY_TO_SECOND_MONEY -> {
                    painterResource(R.drawable.ic_long_arrow_down)
                }
                ConversionMode.SECOND_MONEY_TO_FIRST_MONEY -> {
                    painterResource(R.drawable.ic_long_arrow_up)
                }
            },
            contentDescription = null,
            tint = RedColorScheme.primary
        )
    }
}

@Preview
@Composable
fun ConverterScreenContentPreview() {
    ConverterContent(state = State.Idle)
}
