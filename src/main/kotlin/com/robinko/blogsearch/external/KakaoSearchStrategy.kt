package com.robinko.blogsearch.external

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand
import com.robinko.blogsearch.BlogSearchPriority
import com.robinko.blogsearch.BlogSearchPriorityRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.net.URLEncoder
import java.net.http.HttpClient

/**
 * 카카오 검색 서비스 전략.
 */
@Component
class KakaoSearchStrategy(
    @Value("\${kakao.auth.prefix}")
    private val accessKeyPrefix: String,
    @Value("\${kakao.auth.accessToken}")
    private val accessKey: String,
    @Value("\${kakao.api.host}")
    private val host: String,
    private val blogSearchPriorityRepository: BlogSearchPriorityRepository,
    httpClient: HttpClient
): ExternalStrategyBase(httpClient), SearchBlogStrategy {
    private val log = LoggerFactory.getLogger(KakaoSearchStrategy::class.java)

    private val blogSource = BlogSource.KAKAO

    private val supportedSorts = mapOf("score" to "accuracy", "latest" to "recency")

    private var blogSearchPriority: BlogSearchPriority? = null

    private val authCredentials = setOf("Authorization" ,"$accessKeyPrefix $accessKey")

    override fun getBlogSearchPriority(): BlogSearchPriority? = blogSearchPriority
    override fun setBlogSearchPriority() {
        blogSearchPriority = blogSearchPriorityRepository.findBySource(blogSource)
    }

    override fun getBlogSource(): BlogSource = blogSource

    @HystrixCommand(commandKey = "searchKakaoBlog", fallbackMethod = "searchKakaoBlogFallback")
    override fun searchBlog(
        query: String,
        page: Int,
        size: Int,
        sort: String?
    ): ExternalSearchResult? {
        val refinedSort = sort?.takeIf { it.isNotBlank() && supportedSorts.keys.contains(it) }
            ?.let { supportedSorts[it] }
            ?: supportedSorts.values.first()
        val params = "page=$page&size=$size&query=${URLEncoder.encode(query, Charsets.UTF_8)}&sort=$refinedSort"

        return getExternalResources(
            "/v2/search/blog?$params",
            jacksonTypeRef<KakaoBlogSearchResult>()
        )
    }

    @Suppress("unused")
    fun searchKakaoBlogFallback(
        query: String,
        page: Int = 1,
        size: Int = 20,
        sort: String? = "accuracy",
        ex: Throwable
    ): KakaoBlogSearchResult? {
        log.error("searchKakaoBlogFallback: query $query, page $page, size $size, sort $sort. ex -> ${ex.localizedMessage}")
        return null
    }

    override fun <T> getExternalResources(resourcePath: String, type: TypeReference<T>) =
        getResult(type, request(host, resourcePath, authCredentials))
}