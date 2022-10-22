package com.kwabenaberko.currencyconverter.android.converter.model

import android.os.Parcel
import android.os.Parcelable
import com.kwabenaberko.currencyconverter.domain.model.Currency
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler

@Parcelize
@TypeParceler<Currency, CurrencyParceler>
data class CurrenciesResult(
    val conversionMode: ConversionMode,
    val currency: Currency
) : Parcelable

object CurrencyParceler : Parceler<Currency> {
    override fun create(parcel: Parcel): Currency {
        return with(parcel) {
            Currency(
                code = readString().orEmpty(),
                name = readString().orEmpty(),
                symbol = readString().orEmpty()
            )
        }
    }

    override fun Currency.write(parcel: Parcel, flags: Int) {
        with(parcel) {
            writeString(code)
            writeString(name)
            writeString(symbol)
        }
    }
}
