package com.robinko.blogsearch.naver

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand
import com.robinko.blogsearch.BlogSearchPriority
import com.robinko.blogsearch.BlogSearchPriorityRepository
import com.robinko.blogsearch.BlogSource
import com.robinko.blogsearch.ExternalApiService
import com.robinko.blogsearch.ExternalSearchResult
import com.robinko.blogsearch.SearchBlogStrategy
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.net.URLEncoder
import java.net.http.HttpClient

/**
 * 네이버 검색 서비스 전략.
 */
@Component
class NaverSearchStrategy(
    @Value("\${naver.auth.clientId}")
    private val clientId: String,
    @Value("\${naver.auth.clientSecret}")
    private val clientSecret: String,
    @Value("\${naver.api.host}")
    private val host: String,
    private val blogSearchPriorityRepository: BlogSearchPriorityRepository,
    httpClient: HttpClient,
): ExternalApiService(httpClient), SearchBlogStrategy {
    private val log = LoggerFactory.getLogger(NaverSearchStrategy::class.java)

    final override val blogSource = BlogSource.NAVER

    private val supportedSorts = mapOf("score" to "sim", "latest" to "date")

    private var blogSearchPriority: BlogSearchPriority? = null

    private val authCredentials =
        setOf("X-Naver-Client-Id", clientId, "X-Naver-Client-Secret", clientSecret)

    override fun getBlogSearchPriority(): BlogSearchPriority? = blogSearchPriority

    override fun setBlogSearchPriority() {
        blogSearchPriority = blogSearchPriorityRepository.findBySource(blogSource)
    }

    @HystrixCommand(commandKey = "searchNaverBlog", fallbackMethod = "searchNaverBlogFallback")
    override fun searchBlog(
        query: String,
        page: Int,
        size: Int,
        sort: String?
    ): ExternalSearchResult? {
        val refinedSort = sort?.takeIf { it.isNotBlank() && supportedSorts.keys.contains(it) }
            ?.let { supportedSorts[it] }
            ?: supportedSorts.values.first()
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

    override fun <T> getExternalResources(resourcePath: String, type: TypeReference<T>) =
        getResult(type, request(host, resourcePath, authCredentials))
}