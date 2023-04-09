package com.robinko.blogsearch.external

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.robinko.blogsearch.BlogSearchPriority
import com.robinko.blogsearch.BlogSearchPriorityRepository
import com.robinko.blogsearch.nonNullAny
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.net.http.HttpResponse.BodyHandler

@ExtendWith(MockitoExtension::class)
class NaverSearchStrategyTest {

    private val blogSearchPriorityRepository = Mockito.mock(BlogSearchPriorityRepository::class.java)
    private val httpClient = Mockito.mock(HttpClient::class.java)

    private val naverSearchStrategy = NaverSearchStrategy(
        "X-Naver-Client-Id",
        "X-Naver-Client-Secret",
        "http://localhost",
        blogSearchPriorityRepository,
        httpClient
    )

    private val om = jacksonObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        .registerModules(JavaTimeModule(), Jdk8Module())

    @Test
    fun setBlogSearchPriority() {
        // arrange
        val blogSearchPriorityMock = Mockito.mock(BlogSearchPriority::class.java)
        BDDMockito.given(blogSearchPriorityRepository.findBySource(BlogSource.NAVER))
            .willReturn(blogSearchPriorityMock)

        // action
        var result = naverSearchStrategy.getBlogSearchPriority()
        // verify
        Assertions.assertThat(result).isNull()

        // action
        naverSearchStrategy.setBlogSearchPriority()
        result = naverSearchStrategy.getBlogSearchPriority()
        // verify
        Assertions.assertThat(result).isNotNull
    }

    @Test
    fun `searchBlog when comunication success with naver then return KakaoBlogSearchResult`() {
        // arrange
        val query = "naver"
        val page = 1
        val size = 10
        val sort = "score"
        val responseBody = NaverBlogSearchResult(
            "Sat, 18 Mar 2023 22:48:37 +09:00", 10L, 1, 5,
            listOf(
                NaverBlogDoc("blog", "link", "description", "blogName",
                    "blogLink", "20230301")
            )
        )
        val httpResponseStub = BDDMockito.mock(HttpResponse::class.java).apply {
            BDDMockito.given(statusCode()).willReturn(HttpStatus.OK.value())
            BDDMockito.given(body()).willReturn(om.writeValueAsString(responseBody))
        }
        BDDMockito.given(httpClient.send(nonNullAny(HttpRequest::class.java), nonNullAny(BodyHandler::class.java)))
            .willReturn(httpResponseStub)

        // action
        val result: ExternalSearchResult? = naverSearchStrategy.searchBlog(query, page, size, sort)

        // verify
        Assertions.assertThat(result).isNotNull
        Assertions.assertThat(result).isInstanceOf(NaverBlogSearchResult::class.java)
    }

    @Test
    fun `searchBlog when comunication failure with naver then return null`() {
        // arrange
        val query = "naver"
        val page = 1
        val size = 10
        val sort = "score"
        val httpResponseStub = BDDMockito.mock(HttpResponse::class.java).apply {
            BDDMockito.given(statusCode()).willReturn(HttpStatus.BAD_REQUEST.value())
        }
        BDDMockito.given(httpClient.send(nonNullAny(HttpRequest::class.java), nonNullAny(BodyHandler::class.java)))
            .willReturn(httpResponseStub)

        // action
        val result: ExternalSearchResult? = naverSearchStrategy.searchBlog(query, page, size, sort)

        // verify
        Assertions.assertThat(result).isNull()
    }
}