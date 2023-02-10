package com.kwabenaberko.converter.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CurrenciesDto(
    @SerialName("symbols") val currencies: Map<String, CurrencyDto>
)

@Serializable
data class CurrencyDto(
    @SerialName("code") val code: String,
    @SerialName("description") val name: String,
)

