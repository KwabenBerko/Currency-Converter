package com.kwabenaberko.currencyconverter.domain.repository

import com.kwabenaberko.currencyconverter.domain.model.Currency
import com.kwabenaberko.currencyconverter.domain.model.DefaultCurrencies
import kotlinx.coroutines.flow.Flow

interface CurrencyRepository : SyncableRepository {
    fun currencies(): Flow<List<Currency>>
    fun defaultCurrencies(): Flow<DefaultCurrencies>
    suspend fun setDefaultCurrencies(baseCode: String, targetCode: String)
    suspend fun getRate(baseCode: String, targetCode: String): Double
}
