package com.kwabenaberko.currencyconverter.android.currencies.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kwabenaberko.currencyconverter.android.currencies.CurrenciesViewModel.State
import com.kwabenaberko.currencyconverter.domain.model.Currency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrenciesScreenContent(
    state: State,
    onFilterChange: (String) -> Unit = {},
    onCurrencyClick: (Currency) -> Unit = {}
) {

    when (state) {
        is State.Idle -> Unit
        is State.Content -> {
            Column {
                TextField(value = state.query, onValueChange = onFilterChange)
                Spacer(Modifier.height(10.dp))
                LazyColumn {
                    state.currencies.forEach { (header, currencies) ->
                        item { Text(text = "$header") }
                        items(currencies) { currency ->
                            Text(
                                text = "$currency",
                                modifier = Modifier.clickable { onCurrencyClick(currency) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun CurrenciesScreenContentPreview() {
    CurrenciesScreenContent(state = State.Idle)
}
