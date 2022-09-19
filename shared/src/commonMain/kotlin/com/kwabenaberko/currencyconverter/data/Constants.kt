package com.kwabenaberko.currencyconverter.data

object Database {
    const val NAME = "currency_converter.db"
}

object Settings {
    const val NAME = "currency_converter_settings"
    const val BASE_CODE = "base_code"
    const val TARGET_CODE = "target_code"
    const val CURRENCIES_SYNC_STATUS = "currencies_last_sync_date"
    const val CURRENCIES_LAST_SYNC_DATE = "currencies_last_sync_date"
}

object Api {
    const val BASE_URL = "https://api.exchangerate.host"
    const val CURRENCIES = "/symbols"
    const val EXCHANGE_RATES = "/latest"
    const val CURRENCY_SYMBOLS_URL = "https://gist.githubusercontent.com/NeoGenet1c/d81f67cc0d11f0b843d331e4baaf80cb/raw/3c95d640f8b279893bb5a58b42080a8803a950f6/currency-symbols.json"
}
