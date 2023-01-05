package com.kwabenaberko.currencyconverter.android.converter.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.kwabenaberko.currencyconverter.android.theme.CurrencyConverterTheme

@Composable
internal fun CurrencyAmount(
    formattedAmount: String,
    symbol: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Text(
        text = buildText(
            isPressed = isPressed,
            formattedAmount = formattedAmount,
            symbol = symbol
        ),
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = true,
                onClick = onClick
            ).then(modifier),
        style = MaterialTheme.typography.labelLarge,
    )
}

@Composable
private fun buildText(
    isPressed: Boolean,
    formattedAmount: String,
    symbol: String
): AnnotatedString {
    return buildAnnotatedString {
        withStyle(
            SpanStyle(
                color = if (isPressed) {
                    MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
                } else {
                    MaterialTheme.colorScheme.onPrimary
                },
                fontSize = 88.sp
            )
        ) {
            append(formattedAmount)
        }
        withStyle(
            SpanStyle(
                color = if (isPressed) {
                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                } else {
                    MaterialTheme.colorScheme.secondary
                },
                fontSize = 24.sp
            )
        ) {
            append(symbol)
        }
    }
}

@Preview
@Composable
private fun CurrencyAmountPreview() {
    CurrencyConverterTheme {
        CurrencyAmount(
            formattedAmount = "5M",
            symbol = "GHS"
        )
    }
}
