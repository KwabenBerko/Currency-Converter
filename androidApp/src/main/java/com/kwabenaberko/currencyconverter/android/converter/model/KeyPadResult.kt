package com.kwabenaberko.currencyconverter.android.converter.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class KeyPadResult(val isReverse: Boolean, val amount: Double) : Parcelable
