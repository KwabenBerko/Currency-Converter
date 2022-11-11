package com.kwabenaberko.currencyconverter.android.currencies.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.kwabenaberko.currencyconverter.android.currencies.CurrenciesViewModel.State
import com.kwabenaberko.currencyconverter.android.theme.CurrencyConverterTheme
import com.kwabenaberko.currencyconverter.domain.model.Currency

@Composable
internal fun CurrenciesScreenContent(
    useRedTheme: Boolean,
    state: State,
    onBackClick: () -> Unit = {},
    onFilterQueryChange: (String) -> Unit = {},
    onCurrencyClick: (Currency) -> Unit = {}
) = CurrencyConverterTheme(useRedTheme) {

    val colorScheme = MaterialTheme.colorScheme
    val systemUiController = rememberSystemUiController()

    LaunchedEffect(systemUiController) {
        systemUiController.setStatusBarColor(color = colorScheme.primary)
    }

    when (state) {
        is State.Idle -> Unit
        is State.Content -> {
            Content(
                state = state,
                onBackClick = onBackClick,
                onFilterQueryChange = onFilterQueryChange,
                onCurrencyClick = onCurrencyClick
            )
        }
    }
}

@Composable
private fun Content(
    state: State.Content,
    onBackClick: () -> Unit,
    onFilterQueryChange: (String) -> Unit,
    onCurrencyClick: (Currency) -> Unit,
) {
    var isSearchBarVisible by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Box(Modifier.padding(horizontal = 10.dp, vertical = 4.dp).height(68.dp)) {
            if (isSearchBarVisible) {
                SearchBar(
                    initialValue = "",
                    modifier = Modifier.fillMaxWidth(),
                    onQueryChange = onFilterQueryChange,
                    onClose = {
                        isSearchBarVisible = false
                    }
                )
            } else {
                Toolbar(
                    onBackClick = onBackClick,
                    onSearchClick = {
                        isSearchBarVisible = true
                    },
                    modifier = Modifier.padding(vertical = 4.dp).fillMaxWidth()
                )
            }
        }

        CurrencyList(
            groupedCurrencies = state.currencies,
            modifier = Modifier.padding(horizontal = 20.dp),
            onCurrencyClick = { currency ->
                onCurrencyClick(currency)
            }
        )
    }
}

@Preview
@Composable
internal fun CurrenciesScreenContentPreview() {
    CurrenciesScreenContent(
        useRedTheme = false,
        state = State.Idle
    )
}
