package com.kwabenaberko.converter.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CurrencySymbolDto(
    @SerialName("symbol") val symbol: String
)
