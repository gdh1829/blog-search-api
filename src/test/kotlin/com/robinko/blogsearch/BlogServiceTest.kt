package com.robinko.blogsearch

import com.robinko.blogsearch.external.KakaoBlogSearchResult
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.Mockito.times
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

@ExtendWith(MockitoExtension::class)
class BlogServiceTest {

    @InjectMocks
    private lateinit var blogService: BlogService

    @Mock
    private lateinit var blogSearchService: BlogSearchService
    @Mock
    private lateinit var eventPublisher: ApplicationEventPublisher

    @Test
    fun searchBlog() {
        // arrange
        val query = "apple"
        val pageable = PageRequest.of(1, 20, Sort.Direction.DESC, "score")
        val blocDocMock = Mockito.mock(BlogDoc::class.java)
        val pageImpl = PageImpl(listOf(blocDocMock), pageable, 1L) as Page<BlogDoc>
        val kakaoBlogSearchResultStub = Mockito.mock(KakaoBlogSearchResult::class.java).apply {
            BDDMockito.given(this.toPage(pageable)).willReturn(pageImpl)
        }
        BDDMockito.given(blogSearchService.searchBlog(query, 1, 20, "score"))
            .willReturn(kakaoBlogSearchResultStub)

        // action
        val result = blogService.searchBlog(query, pageable)

        // verify
        assertAll(
            { Assertions.assertThat(result).isNotEmpty },
            {
                verify(blogSearchService, times(1))
                    .searchBlog(query, 1, 20, "score")
            },
            { verify(eventPublisher, times(1)).publishEvent(BlogSearchEvent(query)) }
        )
    }
}