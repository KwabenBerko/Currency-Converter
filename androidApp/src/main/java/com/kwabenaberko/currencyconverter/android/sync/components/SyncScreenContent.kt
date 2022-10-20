package com.kwabenaberko.currencyconverter.android.sync.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.kwabenaberko.currencyconverter.android.sync.SyncViewModel.State

@Composable
fun SyncScreenContent(state: State) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (state) {
            State.Idle -> Text("Idle")
            State.Syncing -> Text("Syncing")
            State.SyncError -> Text("Sync Error")
            State.SyncSuccess -> Text("Sync Success")
        }
    }
}

@Preview
@Composable
fun SyncScreenPreview() {
    SyncScreenContent(state = State.Idle)
}
