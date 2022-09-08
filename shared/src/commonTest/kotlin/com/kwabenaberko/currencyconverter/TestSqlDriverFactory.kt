package com.kwabenaberko.currencyconverter

import app.cash.sqldelight.db.SqlDriver

expect class TestSqlDriverFactory() {
    fun create(): SqlDriver
}
