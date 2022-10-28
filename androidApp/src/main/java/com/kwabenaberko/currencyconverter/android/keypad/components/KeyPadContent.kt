package com.kwabenaberko.currencyconverter.android.keypad.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KeyPadContent(
    amount: String,
    onAmountChange: (String) -> Unit = {},
    onDoneClick: () -> Unit = {}
) {
    Column {
        TextField(
            value = amount,
            onValueChange = onAmountChange,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        TextButton(onClick = onDoneClick) {
            Text("Done")
        }
    }
}

@Preview
@Composable
fun KeyPadScreenContentPreview() {
    KeyPadContent(amount = "100")
}
