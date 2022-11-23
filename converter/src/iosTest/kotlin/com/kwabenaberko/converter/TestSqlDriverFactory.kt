package com.kwabenaberko.converter

import co.touchlab.sqliter.DatabaseConfiguration
import com.kwabenaberko.CurrencyConverterDatabase
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import app.cash.sqldelight.driver.native.wrapConnection

actual class TestSqlDriverFactory {
    actual fun create(): SqlDriver {
        val schema = CurrencyConverterDatabase.Schema
        val configuration = DatabaseConfiguration(
            name = "currencyconverter.db",
            version = schema.version,
            create = { connection ->
                wrapConnection(connection) { driver ->
                    schema.create(driver)
                }
            },
            upgrade = { connection, oldVersion, newVersion ->
                wrapConnection(connection) { driver ->
                    schema.migrate(driver, oldVersion, newVersion)
                }
            },
            inMemory = true
        )
        return NativeSqliteDriver(configuration)
    }
}
