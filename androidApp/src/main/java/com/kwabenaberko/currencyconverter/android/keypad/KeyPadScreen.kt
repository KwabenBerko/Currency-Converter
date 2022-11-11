package com.kwabenaberko.currencyconverter.android.keypad

import androidx.compose.runtime.Composable
import com.kwabenaberko.currencyconverter.android.converter.model.ConversionMode
import com.kwabenaberko.currencyconverter.android.converter.model.KeyPadResult
import com.kwabenaberko.currencyconverter.android.keypad.components.KeyPadScreenContent
import com.kwabenaberko.currencyconverter.android.useRedTheme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator

@Destination
@Composable
fun KeyPadScreen(
    conversionMode: ConversionMode,
    navigator: DestinationsNavigator,
    resultNavigator: ResultBackNavigator<KeyPadResult>
) {
    KeyPadScreenContent(
        useRedTheme = useRedTheme(conversionMode),
        onBackClick = {
            navigator.popBackStack()
        },
        onDone = { amount ->
            resultNavigator.navigateBack(KeyPadResult(conversionMode, amount))
        }
    )
}
