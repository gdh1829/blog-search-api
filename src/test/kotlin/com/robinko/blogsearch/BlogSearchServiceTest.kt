package com.robinko.blogsearch

import com.robinko.blogsearch.kakao.KakaoSearchStrategy
import com.robinko.blogsearch.naver.NaverBlogSearchResult
import com.robinko.blogsearch.naver.NaverSearchStrategy
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class BlogSearchServiceTest {

    @Mock
    private lateinit var kakaoSearchStrategy: KakaoSearchStrategy
    @Mock
    private lateinit var naverSearchStrategy: NaverSearchStrategy

    private lateinit var blogSearchService: BlogSearchService

    @BeforeEach
    fun init() {
        blogSearchService = BlogSearchService(setOf(kakaoSearchStrategy, naverSearchStrategy))
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

    @Test
    fun `searchBlog with two searchSources but kakao failed then search from naver`() {
        // arrange
        val query = "apple"
        val page = 1
        val size = 10
        val sort = "score"
        val naverSearchResultMock = Mockito.mock(NaverBlogSearchResult::class.java)
        kakaoSearchStrategy.apply {
            BDDMockito.given(getBlogSearchPriority())
                .willReturn(BlogSearchPriority(serverId = 0, source = BlogSource.KAKAO, use = true, priority = 0))
        }
        naverSearchStrategy.apply {
            BDDMockito.given(getBlogSearchPriority())
                .willReturn(BlogSearchPriority(serverId = 1, source = BlogSource.NAVER, use = true, priority = 5))
        }
        BDDMockito.given(kakaoSearchStrategy.searchBlog(query, page, size, sort)).willReturn(null)
        BDDMockito.given(naverSearchStrategy.searchBlog(query, page, size, sort)).willReturn(naverSearchResultMock)

        // action
        blogSearchService.refreshSearchBlogStrategyPriorities()
        val result = blogSearchService.searchBlog(query, page, size, sort)

        // verify
        Assertions.assertThat(result).isNotNull
        Assertions.assertThat(result).isInstanceOf(NaverBlogSearchResult::class.java)
        Mockito.verify(kakaoSearchStrategy, Mockito.times(1)).searchBlog(query, page, size, sort)
        Mockito.verify(naverSearchStrategy, Mockito.times(1)).searchBlog(query, page, size, sort)
    }
}
