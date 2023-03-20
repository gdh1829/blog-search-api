package com.robinko.blogsearch

import com.robinko.blogsearch.kakao.KakaoSearchStrategy
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import java.net.http.HttpClient

@ExtendWith(MockitoExtension::class)
class BlogSearchServiceTest {

    @Mock
    private lateinit var blogSearchPriorityRepository: BlogSearchPriorityRepository
    @Mock
    private lateinit var httpClient: HttpClient

    private lateinit var blogSearchService: BlogSearchService

    @BeforeEach
    fun init() {
        blogSearchService = BlogSearchService(
            setOf(KakaoSearchStrategy("", "", "", blogSearchPriorityRepository, httpClient))
        )
    }

    @Test
    fun `searchBlog when searchSources is empty then return null`() {
        // arrange
        val query = "apple"
        val page = 0
        val size = 0
        val sort = "score"

        // action
        val result = blogSearchService.searchBlog(query, page, size, sort)

        // verify
        Assertions.assertThat(result).isNull()
    }

    // @Test
    // fun `searchBlog`() {
    //     // arrange
    //     val query = "apple"
    //     val page = 0
    //     val size = 0
    //     val sort = "score"
    //     BDDMockito.given(blogSearchPriorityRepository.findBySource(SourceBlog.KAKAO))
    //         .willReturn(BlogSearchPriority(serverId = 0, source = SourceBlog.KAKAO, priority = 0))
    //
    //     // action
    //     blogSearchService.refreshSearchBlogStrategyPriorities()
    //     val result = blogSearchService.searchBlog(query, page, size, sort)
    //
    //     // verify
    //     Assertions.assertThat(result).isNull()
    // }
}