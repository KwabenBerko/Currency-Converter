package com.kwabenaberko.converter.domain.repository

import com.kwabenaberko.converter.domain.model.Currency
import com.kwabenaberko.converter.domain.model.DefaultCurrencies
import kotlinx.coroutines.flow.Flow

interface CurrencyRepository : SyncableRepository {
    fun currencies(filter: String?): Flow<List<Currency>>
    suspend fun getDefaultCurrencies(): DefaultCurrencies
    suspend fun setDefaultCurrencies(baseCode: String, targetCode: String)
    suspend fun getRate(baseCode: String, targetCode: String): Double
}
