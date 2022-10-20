package com.kwabenaberko.currencyconverter.android.keypad

import android.os.Bundle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.savedstate.SavedStateRegistryOwner
import com.kwabenaberko.currencyconverter.android.LocalContainer
import com.kwabenaberko.currencyconverter.android.converter.model.KeyPadResult
import com.kwabenaberko.currencyconverter.android.destinations.ProfileScreenDestination
import com.kwabenaberko.currencyconverter.android.keypad.components.KeyPadScreenContent
import com.kwabenaberko.currencyconverter.domain.usecase.GetCurrencies
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator

@Destination
@Composable
fun KeyPadScreen(
    isReverse: Boolean,
    resultNavigator: ResultBackNavigator<KeyPadResult>
) {
    val (amount, setAmount) = remember { mutableStateOf("0") }

    KeyPadScreenContent(
        amount = amount,
        onAmountChange = setAmount,
        onDoneClick = {
            resultNavigator.navigateBack(KeyPadResult(isReverse, amount.toDouble()))
        }
    )
}

@Composable
@Destination(
    navArgsDelegate = ProfileScreenNavArgs::class
)
fun ProfileScreen(
    vm: ProfileViewModel = profileViewModel()
) {
    Text("Profile Screen")
}

@Composable
fun profileViewModel(
    savedStateRegistryOwner: SavedStateRegistryOwner = LocalSavedStateRegistryOwner.current
): ProfileViewModel {
    val container = LocalContainer.current
    val factory = ProfileViewModelFactory(
        owner = savedStateRegistryOwner,
        defaultArgs = (savedStateRegistryOwner as? NavBackStackEntry)?.arguments,
        getCurrencies = container.getCurrencies
    )
    return viewModel(factory = factory)
}

data class ProfileScreenNavArgs(val id: String)
class ProfileViewModel(
    private val getCurrencies: GetCurrencies,
    private val args: ProfileScreenNavArgs
) : ViewModel() {
    init {
        println("Args Is:")
        println("$args")
    }
}

class ProfileViewModelFactory constructor(
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle?,
    private val getCurrencies: GetCurrencies
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        return ProfileViewModel(
            getCurrencies,
            ProfileScreenDestination.argsFrom(handle)
        ) as T
    }
}
