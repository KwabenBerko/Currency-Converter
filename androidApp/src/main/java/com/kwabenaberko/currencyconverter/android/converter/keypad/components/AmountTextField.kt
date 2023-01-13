package com.kwabenaberko.currencyconverter.android.converter.keypad.components

import androidx.compose.foundation.background
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.kwabenaberko.currencyconverter.android.isAtMostMediumHeight
import com.kwabenaberko.currencyconverter.android.isAtMostXhdpi
import com.kwabenaberko.currencyconverter.android.theme.CurrencyConverterTheme

@Composable
internal fun AmountTextField(
    textFieldValue: TextFieldValue,
    modifier: Modifier = Modifier
) {

    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val shouldAdjustSize = density.isAtMostXhdpi() && configuration.isAtMostMediumHeight()

    BasicTextField(
        value = textFieldValue,
        onValueChange = {},
        enabled = false,
        modifier = Modifier.background(MaterialTheme.colorScheme.background).then(modifier),
        textStyle = MaterialTheme.typography.labelLarge.copy(
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = if (shouldAdjustSize) 78.sp else 88.sp,
            textAlign = TextAlign.Center
        ),
        singleLine = true
    )
}

@Preview
@Composable
private fun AmountTextFieldPreview() {
    CurrencyConverterTheme {
        AmountTextField(TextFieldValue(text = "200"))
    }
}
