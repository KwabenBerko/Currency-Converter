package com.kwabenaberko.currencyconverter.android.converter.components

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.kwabenaberko.currencyconverter.android.theme.CurrencyConverterTheme

@Composable
internal fun CurrencyCode(
    code: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = code,
        modifier = Modifier.background(MaterialTheme.colorScheme.background).then(modifier),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.secondary
    )
}

@Preview
@Composable
private fun CurrencyCodePreview() {
    CurrencyConverterTheme {
        CurrencyCode("GHS")
    }
}
