package com.kwabenaberko.converter.factory

import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.kwabenaberko.CurrencyConverterDatabase
import com.kwabenaberko.converter.data.Database
import com.kwabenaberko.converter.data.Settings
import com.russhwolf.settings.NSUserDefaultsSettings
import io.ktor.client.engine.darwin.Darwin
import kotlinx.coroutines.Dispatchers
import platform.Foundation.NSUserDefaults

actual class ContainerFactory {
    actual fun makeContainer(): Container {
        val configuration = nativeDatabaseConfiguration()
        return Container(
            httpClientEngine = Darwin.create(),
            sqlDriver = NativeSqliteDriver(configuration),
            settings = NSUserDefaultsSettings(
                NSUserDefaults(suiteName = Settings.NAME)
            ),
            backgroundDispatcher = Dispatchers.Default
        )
    }
}
