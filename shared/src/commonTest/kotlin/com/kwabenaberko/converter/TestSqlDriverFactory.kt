package com.kwabenaberko.converter

import app.cash.sqldelight.db.SqlDriver

expect class TestSqlDriverFactory() {
    fun create(): SqlDriver
}
