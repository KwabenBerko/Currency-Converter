package com.kwabenaberko.converter.factory

import app.cash.sqldelight.driver.native.wrapConnection
import co.touchlab.sqliter.DatabaseConfiguration
import com.kwabenaberko.CurrencyConverterDatabase
import com.kwabenaberko.converter.data.Database

internal fun nativeDatabaseConfiguration(
    inMemory: Boolean = false
): DatabaseConfiguration {
    val schema = CurrencyConverterDatabase.Schema
    return DatabaseConfiguration(
        name = Database.NAME,
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
        extendedConfig = DatabaseConfiguration.Extended(foreignKeyConstraints = true),
        inMemory = inMemory
    )
}