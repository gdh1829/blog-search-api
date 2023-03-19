package com.robinko.blogsearch

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory

private val log = LoggerFactory.getLogger(BlogSearchApiApplication::class.java)

fun <T> ObjectMapper.readValueOrNull(body: String, clazz: Class<T>) =
    kotlin.runCatching { this.readValue(body, clazz) }
        .onFailure { log.debug("Failed to read body. $body => $clazz", it) }
        .getOrNull()

fun <T> ObjectMapper.convertValueOrNull(fromValue: Any, toValueType: Class<T>): T? =
    kotlin.runCatching { this.convertValue(fromValue, toValueType) }
        .onFailure { log.debug("Failed to convert body. $fromValue => $toValueType", it) }
        .getOrNull()
