package com.kwabenaberko.currencyconverter.android.sync

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kwabenaberko.converter.domain.usecase.Sync
import com.kwabenaberko.converter.presentation.viewmodel.SyncViewModel

class SyncViewModelFactory(
    private val sync: Sync
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SyncViewModel(sync) as T
    }
}
