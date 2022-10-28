package com.kwabenaberko.currencyconverter.android.currencies.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.kwabenaberko.currencyconverter.android.R
import com.kwabenaberko.currencyconverter.android.currencies.CurrenciesViewModel.State
import com.kwabenaberko.currencyconverter.android.isLight
import com.kwabenaberko.currencyconverter.android.theme.CurrencyConverterTheme
import com.kwabenaberko.currencyconverter.domain.model.Currency

@Composable
fun CurrenciesContent(
    useRedTheme: Boolean,
    state: State,
    onBackClick: () -> Unit = {},
    onFilterChange: (String) -> Unit = {},
    onCurrencyClick: (Currency) -> Unit = {}
) = CurrencyConverterTheme(useRedTheme) {

    val colorScheme = MaterialTheme.colorScheme.primary
    val systemUiController = rememberSystemUiController()
    val (showSearchBar, setShowSearchBar) = remember { mutableStateOf(false) }

    LaunchedEffect(systemUiController) {
        systemUiController.setStatusBarColor(
            color = colorScheme,
            darkIcons = colorScheme.isLight()
        )
    }


    when (state) {
        is State.Idle -> Unit
        is State.Content -> {
            Column(
                modifier = Modifier.fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Box(
                    Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        .height(68.dp)
                ) {
                    if (showSearchBar) {
                        SearchBar(
                            value = state.query,
                            modifier = Modifier.fillMaxWidth(),
                            onValueChange = onFilterChange,
                            onClose = {
                                setShowSearchBar(false)
                            }
                        )
                    } else {
                        Toolbar(
                            onBackClick = onBackClick,
                            onSearchClick = {
                                setShowSearchBar(true)
                            },
                            modifier = Modifier.padding(vertical = 4.dp).fillMaxWidth()
                        )
                    }
                }

                CurrencyList(
                    groupedCurrencies = state.currencies,
                    modifier = Modifier.padding(horizontal = 20.dp)
                ) { currency ->
                    onCurrencyClick(currency)
                }
            }
        }
    }
}

@Composable
fun Toolbar(
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        IconButton(onClick = onBackClick) {
            Icon(
                painter = painterResource(R.drawable.ic_long_arrow_left),
                contentDescription = "Go Back",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        IconButton(onClick = onSearchClick) {
            Icon(
                painter = painterResource(R.drawable.ic_search),
                contentDescription = "Filter Currencies",
                tint = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Preview
@Composable
fun CurrenciesScreenContentPreview() {
    CurrenciesContent(
        useRedTheme = false,
        state = State.Idle
    )
}
