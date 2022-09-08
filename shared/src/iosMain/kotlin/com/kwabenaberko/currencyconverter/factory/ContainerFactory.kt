package com.kwabenaberko.currencyconverter.factory

import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.kwabenaberko.CurrencyConverterDatabase
import com.kwabenaberko.currencyconverter.data.Database
import com.kwabenaberko.currencyconverter.data.Settings
import com.russhwolf.settings.NSUserDefaultsSettings
import io.ktor.client.engine.darwin.Darwin
import kotlinx.coroutines.Dispatchers
import platform.Foundation.NSUserDefaults

actual class ContainerFactory {
    actual fun makeContainer(): Container {
        return Container(
            httpClientEngine = Darwin.create(),
            sqlDriver = NativeSqliteDriver(
                schema = CurrencyConverterDatabase.Schema,
                name = Database.NAME
            ),
            settings = NSUserDefaultsSettings(
                NSUserDefaults(suiteName = Settings.NAME)
            ),
            backgroundDispatcher = Dispatchers.Default
        )
    }
}
