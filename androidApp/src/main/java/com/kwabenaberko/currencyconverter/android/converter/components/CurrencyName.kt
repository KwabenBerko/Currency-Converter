package com.kwabenaberko.currencyconverter.android.converter.components

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.kwabenaberko.currencyconverter.android.theme.CurrencyConverterTheme

@Composable
internal fun CurrencyName(
    name: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Text(
        text = name,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = true,
                onClick = onClick
            ).then(modifier),
        style = MaterialTheme.typography.labelLarge.copy(
            fontSize = 24.sp,
            color = if (isPressed) {
                MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.onPrimary
            }
        )
    )
}

@Preview
@Composable
private fun CurrencyNamePreview() {
    CurrencyConverterTheme {
        CurrencyName(name = "Ghanaian Cedi")
    }
}
