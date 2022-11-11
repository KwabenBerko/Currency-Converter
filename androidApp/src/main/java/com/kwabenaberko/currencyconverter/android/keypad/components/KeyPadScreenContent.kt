package com.kwabenaberko.currencyconverter.android.keypad.components

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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.kwabenaberko.currencyconverter.android.R
import com.kwabenaberko.currencyconverter.android.theme.CurrencyConverterTheme

@Composable
internal fun KeyPadScreenContent(
    useRedTheme: Boolean,
    onBackClick: () -> Unit = {},
    onDone: (Double) -> Unit = {}
) = CurrencyConverterTheme(useRedTheme) {

    val colorScheme = MaterialTheme.colorScheme
    val focusRequester = remember { FocusRequester() }
    val systemUiController = rememberSystemUiController()
    val (amount, setAmount) = remember { mutableStateOf(TextFieldValue("")) }
    val isDoneEnabled = remember(amount) { amount.text.isNotEmpty() }

    LaunchedEffect(systemUiController) {
        systemUiController.setStatusBarColor(color = colorScheme.primary)
    }

    DisposableEffect(Unit) {
        focusRequester.requestFocus()
        onDispose {
            focusRequester.freeFocus()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 32.dp, bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        TapToDeleteTextButton {
            val newAmount = amount.text.substring(0, amount.text.length - 1)
            setAmount(
                amount.copy(
                    text = newAmount,
                    selection = TextRange(newAmount.length)
                )
            )
        }

        AmountTextField(
            textFieldValue = amount,
            modifier = Modifier.focusRequester(focusRequester)
        )

        KEYS.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally)
            ) {
                row.forEach { key ->
                    if (key == DONE) {
                        DoneKeyButton(isEnabled = isDoneEnabled) {
                            onDone(amount.text.toDouble())
                        }
                    } else {
                        TextKeyButton(text = key) {
                            val newAmount = amount.text + key
                            setAmount(
                                amount.copy(
                                    text = newAmount,
                                    selection = TextRange(newAmount.length)
                                )
                            )
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
    KeyPadScreenContent(useRedTheme = true)
}
