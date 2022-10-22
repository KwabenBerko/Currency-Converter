package com.kwabenaberko.currencyconverter.android.converter.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kwabenaberko.currencyconverter.android.converter.ConverterViewModel.State
import com.kwabenaberko.currencyconverter.android.converter.model.ConversionMode
import com.kwabenaberko.currencyconverter.domain.model.Currency

@Composable
fun ConverterScreenContent(
    state: State,
    onFirstCurrencyClick: (Currency) -> Unit = {},
    onFirstAmountClick: () -> Unit = {},
    onSecondCurrencyClick: (Currency) -> Unit = {},
    onSecondAmountClick: () -> Unit = {}
) {

    when (state) {
        is State.Idle -> Unit
        is State.Content -> {
            val firstMoneyItem = state.firstMoneyItem
            val secondMoneyItem = state.secondMoneyItem
            Column {
                Text(
                    text = "${firstMoneyItem.money.currency}",
                    modifier = Modifier.clickable {
                        onFirstCurrencyClick(firstMoneyItem.money.currency)
                    }
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    text = firstMoneyItem.formattedAmount,
                    modifier = Modifier.clickable { onFirstAmountClick() }
                )

                Spacer(Modifier.height(30.dp))
                Text(
                    text = when (state.conversionMode) {
                        ConversionMode.FIRST_MONEY_TO_SECOND_MONEY -> "First To Second"
                        ConversionMode.SECOND_MONEY_TO_FIRST_MONEY -> "Second To First"
                    }
                )
                Spacer(Modifier.height(30.dp))

                Text(
                    text = "$secondMoneyItem",
                    modifier = Modifier.clickable { onSecondAmountClick() }
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    text = "${secondMoneyItem.money.currency}",
                    modifier = Modifier.clickable {
                        onSecondCurrencyClick(secondMoneyItem.money.currency)
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun ConverterScreenContentPreview() {
    ConverterScreenContent(state = State.Idle)
}
