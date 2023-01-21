package com.kwabenaberko.currencyconverter.android.converter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kwabenaberko.converter.domain.usecase.ConvertMoney
import com.kwabenaberko.converter.domain.usecase.GetDefaultCurrencies
import com.kwabenaberko.converter.domain.usecase.HasCompletedInitialSync
import com.kwabenaberko.converter.presentation.viewmodel.ConverterViewModel

class ConverterViewModelFactory(
    private val hasCompletedInitialSync: HasCompletedInitialSync,
    private val getDefaultCurrencies: GetDefaultCurrencies,
    private val convertMoney: ConvertMoney
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ConverterViewModel(
            hasCompletedInitialSync = hasCompletedInitialSync,
            getDefaultCurrencies = getDefaultCurrencies,
            convertMoney = convertMoney
        ) as T
    }
}
