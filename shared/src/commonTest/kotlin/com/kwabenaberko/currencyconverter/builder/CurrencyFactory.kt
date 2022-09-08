package com.kwabenaberko.currencyconverter.builder

import com.kwabenaberko.currencyconverter.domain.model.Currency

object CurrencyFactory {
    fun makeCurrency(
        code: String = "",
        name: String = "",
        symbol: String = ""
    ): Currency {
        return Currency(code, name, symbol)
    }

    fun makeDollarCurrency(): Currency {
        return makeCurrency(
            code = "USD",
            name = "United States Dollar",
            symbol = "$"
        )
    }

    fun makeCediCurrency(): Currency {
        return makeCurrency(
            code = "GHS",
            name = "Ghanaian Cedi",
            symbol = "GH₵"
        )
    }

    fun makeNairaCurrency(): Currency {
        return makeCurrency(
            code = "NGN",
            name = "Nigerian Naira",
            symbol = "₦"
        )
    }

    fun makePoundsCurrency(): Currency {
        return makeCurrency(
            code = "GBP",
            name = "British Pounds",
            symbol = "£"
        )
    }

    fun makeEuroCurrency(): Currency {
        return makeCurrency(
            code = "EUR",
            name = "Euro",
            symbol = "€"
        )
    }
}
