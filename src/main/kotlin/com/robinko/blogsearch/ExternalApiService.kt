package com.robinko.blogsearch

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

/**
 * 외부 연동 서비스 호출에 대한 추상 클래스.
 */
abstract class ExternalApiService(
    private val httpClient: HttpClient
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    private val om = jacksonObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        .registerModules(JavaTimeModule(), Jdk8Module())

    abstract fun <T> getExternalResources(resourcePath: String, type: TypeReference<T>): T?

    internal fun <T> getResult(type: TypeReference<T>, response: HttpResponse<String>): T? =
        response.takeIf { it.statusCode() == HttpStatus.OK.value() }
            ?.body()
            ?.takeIf { it.isNotBlank() }
            ?.let { body ->
                log.debug("external api response body: $body")
                runCatching { om.readValue(body, type) }
                    .onFailure { log.error("failed to parse body => ${it.localizedMessage}") }
                    .getOrNull()
            }

    internal fun request(
        host: String,
        apiPath: String,
        headers: Set<String>
    ): HttpResponse<String> {
        val request = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create("$host$apiPath"))
            .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .headers(*headers.toTypedArray())
            .build()

        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
        if (response.statusCode() != HttpStatus.OK.value()) {
            log.error("Api Connection Error -> $response ${response.body()}")
        }

        return response
    }

}