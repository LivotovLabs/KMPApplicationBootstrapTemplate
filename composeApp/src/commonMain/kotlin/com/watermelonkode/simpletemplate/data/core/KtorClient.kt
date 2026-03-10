package com.watermelonkode.simpletemplate.data.core

import androidx.compose.ui.text.intl.Locale
import com.outsidesource.oskitkmp.outcome.Outcome
import com.watermelonkode.simpletemplate.domain.service.LoggingService
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

/**
 * A wrapper around Ktor's [HttpClient] providing a convenient and unified way to call HTTP endpoints.
 *
 * This client is pre-configured with:
 * - **Content Negotiation**: Uses Kotlinx Serialization with a custom JSON formatter.
 * - **Enhanced Logging**: Integrated with [LoggingService], including automatic pretty-printing of JSON request/response bodies.
 * - **Outcome Pattern**: Standardized error handling returning [Outcome] with [HttpError].
 *
 * It is a convenient way to make network calls in KMP because it abstracts away Ktor boilerplate,
 * provides platform-agnostic logging, and ensures type-safe request/response handling.
 */
class KtorClient(private val logService: LoggingService) {

    private val tag = "KtorClient"

    private val jsonFormatter = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        isLenient = true
    }

    val client = createHttpClient()

    private fun createHttpClient(): HttpClient = HttpClient {
        install(ContentNegotiation) {
            json(jsonFormatter)
        }

        install(Logging) {
            level = LogLevel.ALL
            logger = object : Logger {
                override fun log(message: String) {
                    if (!message.contains("BODY START")) {
                        logService.debug(tag) { message }
                        return
                    }

                    val bodyContent = extractBody(message) ?: run {
                        logService.debug(tag) { message }
                        return
                    }

                    if (bodyContent.isBlank()) {
                        logService.debug(tag) { message }
                        return
                    }

                    try {
                        val prettyJson = jsonFormatter.encodeToString(
                            JsonElement.serializer(),
                            Json.parseToJsonElement(bodyContent),
                        )
                        logService.debug(tag) { message.replace(bodyContent, "FORMATTED BODY:\n$prettyJson") }
                    } catch (e: Exception) {
                        logService.error(tag, e) { "Error formatting body: ${e.message}\nmessage" }
                    }
                }

                private fun extractBody(message: String): String? {
                    return message
                        .substringAfter("BODY START", "")
                        .substringBefore("BODY END", "")
                        .takeIf { it.isNotBlank() }
                        ?.trim()
                }
            }
        }
    }
}

/**
 * Performs a GET request.
 *
 * @param url The endpoint URL.
 * @param block Optional configuration block for the [HttpRequestBuilder].
 * @return An [Outcome] containing the parsed body [T] or an [HttpError].
 */
suspend inline fun <reified T> KtorClient.get(
    url: String,
    block: HttpRequestBuilder.() -> Unit = {},
): Outcome<T, HttpError> {
    return try {
        val res = this.client.get(url, block)
        when (res.status) {
            HttpStatusCode.OK -> Outcome.Ok(res.body<T>())
            else -> Outcome.Error(HttpError.ServerError(res.status.value, res.status.description, res.body<String>()))
        }
    } catch (err: Throwable) {
        Outcome.Error(HttpError.ClientException(err))
    }
}

/**
 * Performs a POST request with a JSON body.
 *
 * @param url The endpoint URL.
 * @param request The object to be serialized as the JSON body.
 * @param block Optional configuration block for the [HttpRequestBuilder].
 * @return An [Outcome] containing the parsed response body [R] or an [HttpError].
 */
suspend inline fun <reified T, reified R> KtorClient.post(
    url: String,
    request: T?,
    block: HttpRequestBuilder.() -> Unit = {},
): Outcome<R, HttpError> {
    return try {
        val res = this.client.post(url) {
            contentType(ContentType.Application.Json)
            request?.let {
                setBody(request)
            }
            block(this)
        }
        when (res.status) {
            HttpStatusCode.OK -> Outcome.Ok(res.body<R>())
            else -> Outcome.Error(HttpError.ServerError(res.status.value, res.status.description, res.body<String>()))
        }
    } catch (err: Throwable) {
        Outcome.Error(HttpError.ClientException(err))
    }
}

/**
 * Performs a PATCH request with a JSON body.
 *
 * @param url The endpoint URL.
 * @param request The object to be serialized as the JSON body.
 * @param block Optional configuration block for the [HttpRequestBuilder].
 * @return An [Outcome] containing the parsed response body [R] or an [HttpError].
 */
suspend inline fun <reified T, reified R> KtorClient.patch(
    url: String,
    request: T?,
    block: HttpRequestBuilder.() -> Unit = {},
): Outcome<R, HttpError> {
    return try {
        val res = this.client.patch(url) {
            contentType(ContentType.Application.Json)
            request?.let {
                setBody(request)
            }
            block(this)
        }
        when (res.status) {
            HttpStatusCode.OK -> Outcome.Ok(res.body<R>())
            else -> Outcome.Error(HttpError.ServerError(res.status.value, res.status.description, res.body<String>()))
        }
    } catch (err: Throwable) {
        Outcome.Error(HttpError.ClientException(err))
    }
}

/**
 * Performs a PUT request with a JSON body.
 *
 * @param url The endpoint URL.
 * @param request The object to be serialized as the JSON body.
 * @param block Optional configuration block for the [HttpRequestBuilder].
 * @return An [Outcome] containing the parsed response body [R] or an [HttpError].
 */
suspend inline fun <reified T, reified R> KtorClient.put(
    url: String,
    request: T?,
    block: HttpRequestBuilder.() -> Unit = {},
): Outcome<R, HttpError> {
    return try {
        val res = this.client.put(url) {
            contentType(ContentType.Application.Json)
            request?.let {
                setBody(request)
            }
            block(this)
        }
        when (res.status) {
            HttpStatusCode.OK -> Outcome.Ok(res.body<R>())
            else -> Outcome.Error(HttpError.ServerError(res.status.value, res.status.description, res.body<String>()))
        }
    } catch (err: Throwable) {
        Outcome.Error(HttpError.ClientException(err))
    }
}

/**
 * Performs a DELETE request.
 *
 * @param url The endpoint URL.
 * @param block Optional configuration block for the [HttpRequestBuilder].
 * @return An [Outcome] containing the parsed response body [T] or an [HttpError].
 */
suspend inline fun <reified T> KtorClient.delete(
    url: String,
    block: HttpRequestBuilder.() -> Unit = {},
): Outcome<T, HttpError> {
    return try {
        val res = this.client.delete(url, block)
        when (res.status) {
            HttpStatusCode.OK -> Outcome.Ok(res.body<T>())
            else -> Outcome.Error(HttpError.ServerError(res.status.value, res.status.description, res.body<String>()))
        }
    } catch (err: Throwable) {
        Outcome.Error(HttpError.ClientException(err))
    }
}

/**
 * Helper to add an Authorization header with a token.
 */
fun HttpRequestBuilder.withAuthToken(token: String) {
    header("Authorization", "Token $token")
}

/**
 * Helper to add an Accept-Language header based on the provided [Locale].
 */
fun HttpRequestBuilder.withLanguage(locale: Locale) {
    header("Accept-Language", locale.language)
}

/**
 * Helper to add a query parameter to the URL.
 */
fun HttpRequestBuilder.withQuery(key: String, value: String) {
    url.parameters.append(key, value)
}

sealed class HttpError {
    data class ServerError(val code: Int, val message: String, val body: String) : HttpError()
    data class ClientException(val cause: Throwable) : HttpError()

    fun message(): String {
        return when (this) {
            is ServerError -> "HTTP Error: $code ($message)"
            is ClientException -> "${cause.message}"
        }
    }
}