package com.kwabenaberko.currencyconverter.android.converter.keypad

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.kwabenaberko.converter.presentation.viewmodel.KeypadViewModel
import com.kwabenaberko.converter.presentation.viewmodel.KeypadViewModel.State
import com.kwabenaberko.currencyconverter.android.R
import com.kwabenaberko.currencyconverter.android.converter.keypad.components.AmountTextField
import com.kwabenaberko.currencyconverter.android.converter.keypad.components.DoneKeyButton
import com.kwabenaberko.currencyconverter.android.converter.keypad.components.TapToDeleteTextButton
import com.kwabenaberko.currencyconverter.android.converter.keypad.components.TextKeyButton
import com.kwabenaberko.currencyconverter.android.isAtMostMediumHeight
import com.kwabenaberko.currencyconverter.android.isAtMostXhdpi
import com.kwabenaberko.currencyconverter.android.theme.CurrencyConverterTheme

@Composable
internal fun KeyPadScreen(
    useRedTheme: Boolean,
    state: State,
    onBackClick: () -> Unit = {},
    onAppend: (String) -> Unit = {},
    onRemoveLast: () -> Unit = {},
    onDone: (Double) -> Unit = {}
) = CurrencyConverterTheme(useRedTheme) {

    val colorScheme = MaterialTheme.colorScheme
    val systemUiController = rememberSystemUiController()
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val shouldAdjustSize = density.isAtMostXhdpi() && configuration.isAtMostMediumHeight()

    val amount = remember(state) {
        TextFieldValue(
            text = state.text,
            selection = TextRange(state.text.length)
        )
    }

    LaunchedEffect(systemUiController) {
        systemUiController.setStatusBarColor(color = colorScheme.primary)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 32.dp, bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        TapToDeleteTextButton(onClick = onRemoveLast)

        AmountTextField(
            textFieldValue = amount,
            fontSize = if (shouldAdjustSize) 78.sp else 88.sp
        )

        KEYS.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally)
            ) {
                row.forEach { key ->
                    if (key == DONE) {
                        DoneKeyButton(
                            buttonSize = if (shouldAdjustSize) 58.dp else 78.dp,
                            isEnabled = state.isValid
                        ) {
                            if (state.isValid) {
                                onDone(amount.text.toDouble())
                            }
                        }
                    } else {
                        TextKeyButton(
                            text = key,
                            buttonSize = if (shouldAdjustSize) 58.dp else 78.dp
                        ) {
                            onAppend(key)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        IconButton(onClick = onBackClick) {
            Icon(
                painter = painterResource(R.drawable.ic_chevron_down),
                contentDescription = "Done",
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

private const val DOT = "."
private const val DONE = "done"
private val KEYS = listOf(
    listOf("1", "2", "3"),
    listOf("4", "5", "6"),
    listOf("7", "8", "9"),
    listOf(DOT, "0", DONE)
)

@Preview
@Composable
private fun KeyPadScreenContentPreview() {
    KeyPadScreen(
        useRedTheme = true,
        state = KeypadViewModel.mockState
    )
}
