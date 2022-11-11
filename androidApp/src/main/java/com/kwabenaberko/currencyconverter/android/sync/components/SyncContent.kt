package com.kwabenaberko.currencyconverter.android.sync.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.kwabenaberko.currencyconverter.android.R
import com.kwabenaberko.currencyconverter.android.sync.SyncViewModel.State
import com.kwabenaberko.currencyconverter.android.theme.CurrencyConverterTheme

@Composable
fun SyncContent(
    state: State,
    navigateToConverter: () -> Unit = {},
    retrySync: () -> Unit = {}
) = CurrencyConverterTheme(useRedTheme = true) {
    val colorScheme = MaterialTheme.colorScheme
    val systemUiController = rememberSystemUiController()

    LaunchedEffect(Unit) {
        systemUiController.setStatusBarColor(colorScheme.primary)
    }

    LaunchedEffect(state) {
        if (state is State.SyncSuccess) {
            navigateToConverter()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.secondary),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (state) {
            State.Idle -> Text("Idle")
            State.Syncing -> Text("Syncing")
            State.SyncError -> {
                val composition by rememberLottieComposition(
                    LottieCompositionSpec.RawRes(R.raw.no_internet)
                )

                Column(
                    modifier = Modifier.padding(horizontal = 64.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LottieAnimation(composition)
                    Button(
                        onClick = retrySync,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(
                            text = "Retry",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.W600
                        )
                    }
                }
            }
            State.SyncSuccess -> Text("Sync Success")
        }
    }
}

@Preview
@Composable
fun SyncScreenContentPreview() {
    SyncContent(state = State.Idle)
}
