package com.kwabenaberko.currencyconverter.android.keypad.components

import androidx.compose.foundation.background
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.kwabenaberko.currencyconverter.android.theme.CurrencyConverterTheme

@Composable
internal fun AmountTextField(
    textFieldValue: TextFieldValue,
    modifier: Modifier = Modifier
) {
    val textSelectionColors = TextSelectionColors(
        handleColor = Color.Transparent,
        backgroundColor = Color.Transparent
    )
    CompositionLocalProvider(
        LocalTextInputService provides null,
        LocalTextSelectionColors provides textSelectionColors
    ) {

        BasicTextField(
            value = textFieldValue,
            onValueChange = { },
            modifier = Modifier.background(MaterialTheme.colorScheme.background).then(modifier),
            textStyle = MaterialTheme.typography.labelLarge.copy(
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 88.sp,
                textAlign = TextAlign.Center
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            cursorBrush = Brush.verticalGradient(
                0.00f to Color.Transparent,
                0.25f to Color.Transparent,
                0.25f to MaterialTheme.colorScheme.secondary,
                0.75f to MaterialTheme.colorScheme.secondary,
                0.75f to Color.Transparent,
                1.00f to Color.Transparent
            ),
        )
    }
}

@Preview
@Composable
private fun AmountTextFieldPreview() {
    CurrencyConverterTheme {
        AmountTextField(TextFieldValue(text = "200"))
    }
}
