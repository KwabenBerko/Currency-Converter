package com.kwabenaberko.currencyconverter

import com.kwabenaberko.currencyconverter.factory.Container
import com.russhwolf.settings.MapSettings
import com.russhwolf.settings.ObservableSettings
import app.cash.sqldelight.db.SqlDriver
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondOk
import kotlinx.coroutines.Dispatchers

class TestContainer(
    val httpClientEngine: HttpClientEngine = MockEngine.create {
        addHandler { respondOk() }
    },
    val sqlDriver: SqlDriver = TestSqlDriverFactory().create(),
    val settings: ObservableSettings = MapSettings()
) : Container(
    httpClientEngine = httpClientEngine,
    sqlDriver = sqlDriver,
    settings = settings,
    backgroundDispatcher = Dispatchers.Default
)
