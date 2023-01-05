package com.kwabenaberko.currencyconverter.android.sync

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.google.accompanist.navigation.animation.composable
import com.kwabenaberko.currencyconverter.android.LocalContainer

const val SyncRoute = "sync"

fun NavController.navigateToSync(navOptions: NavOptions? = null){
    navigate(SyncRoute, navOptions)
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.syncScreen(onNavigateToConverter: () -> Unit) {
    composable(
        route = SyncRoute,
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() }
    ) { backStackEntry ->
        val viewModel = syncViewModel(backStackEntry)
        val state by viewModel.state.collectAsState()

        SyncScreen(
            state = state,
            onRetryClick = viewModel::startSync,
            onSyncCompleted = onNavigateToConverter
        )
    }
}

@Composable
private fun syncViewModel(owner: ViewModelStoreOwner): SyncViewModel {
    return with(LocalContainer.current) {
        val factory = SyncViewModel.Factory(this.sync)
        viewModel(viewModelStoreOwner = owner, factory = factory)
    }
}
