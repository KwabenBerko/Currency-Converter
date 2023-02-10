package com.kwabenaberko.converter

import com.kwabenaberko.CurrencyConverterDatabase
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.util.Properties

actual class TestSqlDriverFactory {
    actual fun create(): SqlDriver {
        val properties = Properties().apply {
            put("foreign_keys", "true")
        }
        val driver = JdbcSqliteDriver(
            url = JdbcSqliteDriver.IN_MEMORY,
            properties = properties
        )
        CurrencyConverterDatabase.Schema.create(driver)
        return driver
    }
}
