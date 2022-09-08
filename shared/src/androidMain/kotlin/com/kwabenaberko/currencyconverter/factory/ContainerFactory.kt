package com.kwabenaberko.currencyconverter.factory

import android.content.Context
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.kwabenaberko.CurrencyConverterDatabase
import com.kwabenaberko.currencyconverter.data.Database
import com.kwabenaberko.currencyconverter.data.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import io.ktor.client.engine.android.Android
import kotlinx.coroutines.Dispatchers

actual class ContainerFactory constructor(private val context: Context) {
    actual fun makeContainer(): Container {
        return Container(
            httpClientEngine = Android.create(),
            sqlDriver = AndroidSqliteDriver(
                schema = CurrencyConverterDatabase.Schema,
                context = context,
                name = Database.NAME
            ),
            settings = SharedPreferencesSettings(
                context.getSharedPreferences(Settings.NAME, Context.MODE_PRIVATE)
            ),
            backgroundDispatcher = Dispatchers.IO
        )
    }
}
