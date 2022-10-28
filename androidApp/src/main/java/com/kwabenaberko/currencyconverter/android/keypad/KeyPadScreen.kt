package com.kwabenaberko.currencyconverter.android.keypad

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.kwabenaberko.currencyconverter.android.converter.model.ConversionMode
import com.kwabenaberko.currencyconverter.android.converter.model.KeyPadResult
import com.kwabenaberko.currencyconverter.android.keypad.components.KeyPadContent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator

@Destination
@Composable
fun KeyPadScreen(
    conversionMode: ConversionMode,
    resultNavigator: ResultBackNavigator<KeyPadResult>
) {
    val (amount, setAmount) = remember { mutableStateOf("0") }

    KeyPadContent(
        amount = amount,
        onAmountChange = setAmount,
        onDoneClick = {
            resultNavigator.navigateBack(KeyPadResult(conversionMode, amount.toDouble()))
        }
    )
}
