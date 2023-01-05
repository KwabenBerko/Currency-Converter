package com.kwabenaberko.currencyconverter.android.converter.currencies.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kwabenaberko.converter.domain.model.Currency
import com.kwabenaberko.convertertest.builder.CurrencyFactory
import com.kwabenaberko.currencyconverter.android.theme.CurrencyConverterTheme

private const val EMPTY_STRING = " "

@Composable
internal fun CurrencyItem(
    currency: Currency,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(modifier = Modifier.weight(1f)) {
            Text(
                text = buildCurrencyTitleText(currency),
                style = MaterialTheme.typography.labelLarge,
            )
        }

        Spacer(Modifier.weight(0.2f))

        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected Currency",
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
@ReadOnlyComposable
private fun buildCurrencyTitleText(currency: Currency): AnnotatedString {
    return buildAnnotatedString {
        withStyle(SpanStyle(color = MaterialTheme.colorScheme.onPrimary)) {
            append(currency.name)
            append(EMPTY_STRING)
        }
        withStyle(SpanStyle(color = MaterialTheme.colorScheme.secondary)) {
            append(EMPTY_STRING)
            append(currency.code)
        }
    }
}

@Preview(showBackground = true)
@Composable
internal fun CurrencyItemPreview() {
    CurrencyConverterTheme {
        CurrencyItem(
            currency = CurrencyFactory.makeCediCurrency(),
            isSelected = true
        )
    }
}
