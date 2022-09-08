package com.kwabenaberko.currencyconverter

import com.kwabenaberko.CurrencyConverterDatabase
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver

actual class TestSqlDriverFactory {
    actual fun create(): SqlDriver {
        val schema = CurrencyConverterDatabase.Schema
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        schema.create(driver)
        return driver
    }
}
