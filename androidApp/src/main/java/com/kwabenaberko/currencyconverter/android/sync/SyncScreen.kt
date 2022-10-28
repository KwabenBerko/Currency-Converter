package com.kwabenaberko.currencyconverter.android.sync

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kwabenaberko.currencyconverter.android.LocalContainer
import com.kwabenaberko.currencyconverter.android.destinations.ConverterScreenDestination
import com.kwabenaberko.currencyconverter.android.destinations.SyncScreenDestination
import com.kwabenaberko.currencyconverter.android.sync.components.SyncContent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.popUpTo

@RootNavGraph(start = true)
@Destination
@Composable
fun SyncScreen(
    navigator: DestinationsNavigator,
    viewModel: SyncViewModel = syncViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state) {
        if (state is SyncViewModel.State.SyncSuccess) {
            navigator.navigate(ConverterScreenDestination) {
                popUpTo(SyncScreenDestination) {
                    inclusive = true
                }
            }
        }
    }

    SyncContent(state = state)
}

@Composable
private fun syncViewModel(): SyncViewModel {
    return with(LocalContainer.current) {
        val factory = SyncViewModel.Factory(
            hasCompletedInitialSync = this.hasCompletedInitialSync,
            sync = this.sync
        )
        viewModel(factory = factory)
    }
}
