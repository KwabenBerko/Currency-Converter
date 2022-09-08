package com.kwabenaberko.currencyconverter.data.network

import com.kwabenaberko.currencyconverter.data.Api
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.headers
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.appendIfNameAbsent
import kotlinx.serialization.json.Json

object HttpClientFactory {
    fun makeClient(httpClientEngine: HttpClientEngine): HttpClient {
        return HttpClient(httpClientEngine) {
            install(DefaultRequest) {
                url(Api.BASE_URL)
                headers {
                    appendIfNameAbsent(
                        HttpHeaders.ContentType,
                        ContentType.Application.Json.toString()
                    )
                }
            }

            install(ContentNegotiation) {
                json(
                    json = Json {
                        isLenient = true
                        prettyPrint = true
                    }
                )
            }

            install(Logging) {
                logger = Logger.SIMPLE
                level = LogLevel.ALL
            }
        }
    }
}
