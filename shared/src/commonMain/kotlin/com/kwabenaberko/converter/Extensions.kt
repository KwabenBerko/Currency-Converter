package com.kwabenaberko.converter

fun Double.toPlaces(places: Int): Double {
    return this.round(maximumFractionDigits = places)
}

fun Double.toSignificantDigits(digits: Int): Double {
    return this.round(maximumSignificantDigits = digits)
}

private fun Double.round(
    maximumFractionDigits: Int? = null,
    maximumSignificantDigits: Int? = null
): Double {
    val formatter = DecimalFormatter(
        maximumFractionDigits = maximumFractionDigits,
        maximumSignificantDigits = maximumSignificantDigits
    )
    val formatted = formatter.format(this)
    return formatter.parse(formatted)
}
