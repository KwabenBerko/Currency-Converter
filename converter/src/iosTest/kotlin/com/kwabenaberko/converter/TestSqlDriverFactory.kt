package com.kwabenaberko.converter

import co.touchlab.sqliter.DatabaseConfiguration
import com.kwabenaberko.CurrencyConverterDatabase
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import app.cash.sqldelight.driver.native.wrapConnection
import com.kwabenaberko.converter.factory.nativeDatabaseConfiguration

actual class TestSqlDriverFactory {
    actual fun create(): SqlDriver {
        val configuration = nativeDatabaseConfiguration(inMemory = true)
        return NativeSqliteDriver(configuration)
    }
}
