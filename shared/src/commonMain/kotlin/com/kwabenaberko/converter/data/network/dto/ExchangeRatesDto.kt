package com.kwabenaberko.converter.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExchangeRatesDto(
    @SerialName("base") val baseCode: String,
    @SerialName("rates") val rates: Map<String, Double>
)
