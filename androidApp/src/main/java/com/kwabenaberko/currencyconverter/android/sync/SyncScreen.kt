package com.kwabenaberko.currencyconverter.android.sync

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.kwabenaberko.converter.presentation.viewmodel.SyncViewModel.State
import com.kwabenaberko.currencyconverter.android.theme.CurrencyConverterTheme

@OptIn(ExperimentalAnimationApi::class)
@Composable
internal fun SyncScreen(
    state: State,
    onSyncCompleted: () -> Unit = {},
    onRetryClick: () -> Unit = {}
) = CurrencyConverterTheme(useRedTheme = true) {

    val colorScheme = MaterialTheme.colorScheme
    val systemUiController = rememberSystemUiController()

    LaunchedEffect(Unit) {
        systemUiController.setStatusBarColor(colorScheme.primary)
    }

    LaunchedEffect(state) {
        if (state is State.SyncSuccess) {
            onSyncCompleted()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AnimatedContent(targetState = state) { targetState ->
            when (targetState) {
                State.Idle,
                State.Syncing -> Syncing()
                State.SyncSuccess -> Unit
                State.SyncError -> Error(onRetryClick = onRetryClick)
            }
        }
    }
}

@Composable
internal fun Syncing(modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier = modifier,
            color = MaterialTheme.colorScheme.onPrimary,
            strokeWidth = 8.dp
        )
    }
}

@Composable
internal fun Error(
    modifier: Modifier = Modifier,
    onRetryClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 64.dp).then(modifier),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Uhh ho! Something went wrong! Please retry",
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.labelMedium,
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onRetryClick,
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onPrimary,
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = "Retry",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.W600
            )
        }
    }
}

@Preview
@Composable
fun SyncScreenContentPreview() {
    SyncScreen(state = State.Idle)
}
