package com.kwabenaberko.currencyconverter.android.converter.currencies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kwabenaberko.converter.domain.usecase.GetCurrencies
import com.kwabenaberko.converter.presentation.viewmodel.CurrenciesViewModel

class CurrenciesViewModelFactory(
    private val selectedCurrencyCode: String,
    private val getCurrencies: GetCurrencies
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CurrenciesViewModel(selectedCurrencyCode, getCurrencies) as T
    }
}
