package com.kwabenaberko.currencyconverter.android.keypad.components

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
import com.kwabenaberko.currencyconverter.android.theme.CurrencyConverterTheme

@Composable
internal fun TapToDeleteTextButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Text(
        text = "tap to delete",
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ).then(modifier),
        style = MaterialTheme.typography.labelMedium,
        color = if (isPressed) {
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)
        } else {
            MaterialTheme.colorScheme.secondary
        }
    )
}

@Preview
@Composable
private fun TapToDeleteTextButtonPreview() {
    CurrencyConverterTheme {
        TapToDeleteTextButton(onClick = {})
    }
}