package com.kwabenaberko.currencyconverter.android.converter.currencies.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kwabenaberko.converter.domain.model.Currency
import com.kwabenaberko.currencyconverter.android.theme.CurrencyConverterTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun CurrencyList(
    groupedCurrencies: Map<String, List<Currency>>,
    selectedCurrency: Currency,
    modifier: Modifier = Modifier,
    onCurrencyClick: (Currency) -> Unit = {}
) {
    LazyColumn(
        modifier = modifier.background(MaterialTheme.colorScheme.background)
    ) {
        groupedCurrencies.forEach { (header, currencies) ->
            stickyHeader(key = header) {
                Text(
                    text = header,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(16.dp)
                        .animateItemPlacement()
                )
                Divider()
            }
            items(
                items = currencies,
                key = { currency -> currency.code }
            ) { currency ->
                CurrencyItem(
                    currency = currency,
                    isSelected = selectedCurrency == currency,
                    modifier = Modifier.animateItemPlacement(),
                    onClick = { onCurrencyClick(currency) }
                )
                Divider()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
internal fun CurrencyListPreview() {
    CurrencyConverterTheme {
        val ghs = Currency("GHS", name = "Ghanaian Cedi", symbol = "GH₵")
        val usd = Currency("USD", name = "United States Dollar", symbol = "$")
        val ngn = Currency("NGN", name = "Nigerian Naira", symbol = "₦")
        val groupedCurrencies = mapOf(
            "G" to listOf(ghs),
            "N" to listOf(ngn),
            "U" to listOf(usd)
        )
        CurrencyList(
            groupedCurrencies = groupedCurrencies,
            selectedCurrency = ghs
        )
    }
}
