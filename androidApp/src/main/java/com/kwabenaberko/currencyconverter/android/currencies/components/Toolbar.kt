package com.kwabenaberko.currencyconverter.android.currencies.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.kwabenaberko.currencyconverter.android.R
import com.kwabenaberko.currencyconverter.android.theme.CurrencyConverterTheme

@Composable
internal fun Toolbar(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onSearchClick: () -> Unit = {}
) {
    Row(
        modifier = modifier.background(MaterialTheme.colorScheme.background),
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
                contentDescription = "Search Currencies",
                tint = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Preview
@Composable
internal fun ToolbarPreview() {
    CurrencyConverterTheme {
        Toolbar(modifier = Modifier.fillMaxWidth())
    }
}
