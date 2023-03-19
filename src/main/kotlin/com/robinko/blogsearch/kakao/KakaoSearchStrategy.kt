package com.robinko.blogsearch.kakao

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand
import com.robinko.blogsearch.ExternalApiService
import com.robinko.blogsearch.ExternalSearchResult
import com.robinko.blogsearch.SearchBlogStrategy
import com.robinko.blogsearch.SourceBlog
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.net.URLEncoder
import javax.annotation.PostConstruct

@Component
class KakaoSearchStrategy(
    @Value("\${kakao.auth.prefix}")
    private val accessKeyPrefix: String,
    @Value("\${kakao.auth.accessToken}")
    private val accessKey: String,
    @Value("\${kakao.api.host}")
    private val host: String
): ExternalApiService(), SearchBlogStrategy {
    private val log = LoggerFactory.getLogger(KakaoSearchStrategy::class.java)

    private val supportedSorts = mapOf("score" to "accuracy", "latest" to "recency")
    private lateinit var authCredentials: Set<String>

    @PostConstruct
    fun initAuthHeader() {
        authCredentials = setOf("Authorization" ,"$accessKeyPrefix $accessKey")
    }

    override fun getSourceBlog(): SourceBlog = SourceBlog.KAKAO

    @HystrixCommand(commandKey = "searchkakaoBlog", fallbackMethod = "searchkakaoBlogFallback")
    override fun searchBlog(
        query: String,
        page: Int,
        size: Int,
        sort: String?
    ): ExternalSearchResult? {
        val refinedSort = sort?.takeIf { it.isNotBlank() && supportedSorts.keys.contains(it) } ?: supportedSorts.values.first()
        val params = "page=$page&size=$size&query=${URLEncoder.encode(query, Charsets.UTF_8)}&sort=$refinedSort"

        return getExternalResources(
            "/v2/search/blog?$params",
            jacksonTypeRef<KakaoBlogSearchResult>()
        )
    }

    @Suppress("unused")
    fun searchkakaoBlogFallback(
        query: String,
        page: Int = 1,
        size: Int = 20,
        sort: String? = "accuracy",
        ex: Throwable
    ): KakaoBlogSearchResult? {
        log.error("searchkakaoBlogFallback: query $query, page $page, size $size, sort $sort. ex -> ${ex.localizedMessage}")
        return null
    }

    override fun <T> getExternalResources(resourcePath: String, type: TypeReference<T>) =
        getResult(type, request(host, resourcePath, authCredentials))
}