package com.kwabenaberko.converter.domain.repository

import com.kwabenaberko.converter.domain.model.Currency
import com.kwabenaberko.converter.domain.model.DefaultCurrencies
import kotlinx.coroutines.flow.Flow

interface CurrencyRepository : SyncableRepository {
    fun getCurrencies(filter: String?): Flow<List<Currency>>
    fun getDefaultCurrencies(): Flow<DefaultCurrencies>
    suspend fun updateDefaultCurrencies(baseCode: String, targetCode: String)
    fun getRate(baseCode: String, targetCode: String): Flow<Double>
}
