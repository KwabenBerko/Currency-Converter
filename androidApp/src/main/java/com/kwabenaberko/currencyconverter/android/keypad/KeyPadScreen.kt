package com.kwabenaberko.currencyconverter.android.keypad

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kwabenaberko.currencyconverter.android.converter.model.ConversionMode
import com.kwabenaberko.currencyconverter.android.converter.model.KeyPadResult
import com.kwabenaberko.currencyconverter.android.keypad.animation.KeyPadScreenTransitions
import com.kwabenaberko.currencyconverter.android.keypad.components.KeyPadScreenContent
import com.kwabenaberko.currencyconverter.android.useRedTheme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator

@Destination(style = KeyPadScreenTransitions::class)
@Composable
fun AnimatedVisibilityScope.KeyPadScreen(
    conversionMode: ConversionMode,
    navigator: DestinationsNavigator,
    resultNavigator: ResultBackNavigator<KeyPadResult>,
    viewModel: KeypadViewModel = keypadViewModel()
) {
    val state by viewModel.state.collectAsState()

    KeyPadScreenContent(
        useRedTheme = useRedTheme(conversionMode),
        state = state,
        onBackClick = {
            navigator.popBackStack()
        },
        onAppend = viewModel::append,
        onUndo = viewModel::undo,
        onDone = { amount ->
            resultNavigator.navigateBack(KeyPadResult(conversionMode, amount))
        }
    )
}

@Composable
private fun keypadViewModel(): KeypadViewModel {
    return viewModel()
}
