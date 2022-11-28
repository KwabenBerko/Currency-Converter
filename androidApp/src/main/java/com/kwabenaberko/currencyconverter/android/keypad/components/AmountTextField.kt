package com.kwabenaberko.currencyconverter.android.keypad.components

import androidx.compose.foundation.background
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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

    BasicTextField(
        value = textFieldValue,
        onValueChange = {},
        enabled = false,
        modifier = Modifier.background(MaterialTheme.colorScheme.background).then(modifier),
        textStyle = MaterialTheme.typography.labelLarge.copy(
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 88.sp,
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
