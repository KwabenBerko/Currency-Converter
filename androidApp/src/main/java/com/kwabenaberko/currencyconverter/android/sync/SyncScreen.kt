package com.kwabenaberko.currencyconverter.android.sync

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kwabenaberko.currencyconverter.android.LocalContainer
import com.kwabenaberko.currencyconverter.android.destinations.ConverterScreenDestination
import com.kwabenaberko.currencyconverter.android.destinations.SyncScreenDestination
import com.kwabenaberko.currencyconverter.android.sync.components.SyncScreenContent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.popUpTo

@Destination(style = SyncScreenTransitions::class)
@Composable
fun SyncScreen(
    navigator: DestinationsNavigator,
    viewModel: SyncViewModel = syncViewModel()
) {
    val state by viewModel.state.collectAsState()

    SyncScreenContent(
        state = state,
        onSyncCompleted = {
            navigator.navigate(ConverterScreenDestination) {
                popUpTo(SyncScreenDestination) {
                    inclusive = true
                }
            }
        },
        onRetryClick = viewModel::startSync
    )
}

@Composable
private fun syncViewModel(): SyncViewModel {
    return with(LocalContainer.current) {
        val factory = SyncViewModel.Factory(this.sync)
        viewModel(factory = factory)
    }
}
