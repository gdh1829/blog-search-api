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
import java.time.ZonedDateTime

@ExtendWith(MockitoExtension::class)
class KakaoSearchStrategyTest {

    private val blogSearchPriorityRepository = Mockito.mock(BlogSearchPriorityRepository::class.java)
    private val httpClient = Mockito.mock(HttpClient::class.java)

    private val kakaoSearchStrategy = KakaoSearchStrategy(
        "KakaoAK",
        "accessKey",
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
        BDDMockito.given(blogSearchPriorityRepository.findBySource(BlogSource.KAKAO))
            .willReturn(blogSearchPriorityMock)

        // action
        var result = kakaoSearchStrategy.getBlogSearchPriority()
        // verify
        Assertions.assertThat(result).isNull()

        // action
        kakaoSearchStrategy.setBlogSearchPriority()
        result = kakaoSearchStrategy.getBlogSearchPriority()
        // verify
        Assertions.assertThat(result).isNotNull
    }

    @Test
    fun `searchBlog when comunication success with kakao then return KakaoBlogSearchResult`() {
        // arrange
        val query = "kakao"
        val page = 1
        val size = 10
        val sort = "score"
        val responseBody = KakaoBlogSearchResult(
            SearchMeta(10L, 10L, false),
            listOf(KakaoBlogDoc("blog", "contents", ZonedDateTime.now(), null, "title", "url"))
        )
        val httpResponseStub = BDDMockito.mock(HttpResponse::class.java).apply {
            BDDMockito.given(statusCode()).willReturn(HttpStatus.OK.value())
            BDDMockito.given(body()).willReturn(om.writeValueAsString(responseBody))
        }
        BDDMockito.given(httpClient.send(nonNullAny(HttpRequest::class.java), nonNullAny(BodyHandler::class.java)))
            .willReturn(httpResponseStub)

        // action
        val result: ExternalSearchResult? = kakaoSearchStrategy.searchBlog(query, page, size, sort)

        // verify
        Assertions.assertThat(result).isNotNull
        Assertions.assertThat(result).isInstanceOf(KakaoBlogSearchResult::class.java)
    }

    @Test
    fun `searchBlog when comunication failure with kakao then return null`() {
        // arrange
        val query = "kakao"
        val page = 1
        val size = 10
        val sort = "score"
        val httpResponseStub = BDDMockito.mock(HttpResponse::class.java).apply {
            BDDMockito.given(statusCode()).willReturn(HttpStatus.BAD_REQUEST.value())
        }
        BDDMockito.given(httpClient.send(nonNullAny(HttpRequest::class.java), nonNullAny(BodyHandler::class.java)))
            .willReturn(httpResponseStub)

        // action
        val result: ExternalSearchResult? = kakaoSearchStrategy.searchBlog(query, page, size, sort)

        // verify
        Assertions.assertThat(result).isNull()
    }
}