package com.robinko.blogsearch.naver

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand
import com.robinko.blogsearch.ExternalApiService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.net.URLEncoder
import javax.annotation.PostConstruct

@Service
class NaverSearchService(
    @Value("\${naver.auth.clientId}")
    private val clientId: String,
    @Value("\${naver.auth.clientSecret}")
    private val clientSecret: String,
    @Value("\${naver.api.host}")
    private val host: String
): ExternalApiService() {
    private val log = LoggerFactory.getLogger(NaverSearchService::class.java)

    private val supportedSorts = mapOf("score" to "sim", "latest" to "date")

    private lateinit var authCredentials: Set<String>

    @PostConstruct
    fun initCredentials() {
        authCredentials = setOf("X-Naver-Client-Id", clientId, "X-Naver-Client-Secret", clientSecret)
    }

    @HystrixCommand(commandKey = "searchNaverBlog", fallbackMethod = "searchNaverBlogFallback")
    fun searchNaverBlog(
        query: String,
        page: Int = 1,
        size: Int = 20,
        sort: String? = "sim"
    ): NaverBlogSearchResult? {
        val refinedSort = sort?.takeIf { it.isNotBlank() && supportedSorts.keys.contains(it) } ?: supportedSorts.values.first()
        val params = "page=$page&display=$size&query=${URLEncoder.encode(query, Charsets.UTF_8)}&sort=$refinedSort"

        return getExternalResources(
            "/v1/search/blog?$params",
            jacksonTypeRef<NaverBlogSearchResult>()
        )
    }

    @Suppress("unused")
    fun searchNaverBlogFallback(
        query: String,
        page: Int = 1,
        size: Int = 20,
        sort: String? = "sim",
        ex: Throwable
    ): NaverBlogSearchResult? {
        log.error("searchNaverBlogFallback: query $query, page $page, size $size, sort $sort. ex -> ${ex.localizedMessage}")
        return null
    }

    private fun <T> getExternalResources(resourcePath: String, type: TypeReference<T>) =
        getResult(type, request(host, resourcePath, authCredentials))
}