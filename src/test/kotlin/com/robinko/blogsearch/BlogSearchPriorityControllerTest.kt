package com.robinko.blogsearch

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.mockito.BDDMockito.given
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(controllers = [BlogSearchPriorityController::class])
class BlogSearchPriorityControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var blogSearchService: BlogSearchService

    @Test
    fun `applyBlogSearchPriority success`() {
        // arrange
        given(blogSearchService.refreshSearchBlogStrategyPriorities())
            .willReturn(listOf(BlogSource.KAKAO, BlogSource.NAVER))

        // action
        mockMvc.perform(
             put("/blogSearchPriority")
                 .param("refresh", "true")
                 .contentType(MediaType.APPLICATION_JSON)
                 .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(
                MockMvcResultMatchers.handler().methodName(BlogSearchPriorityController::applyBlogSearchPriority.name)
            )
            .andExpect(MockMvcResultMatchers.content().string("[\"KAKAO\",\"NAVER\"]"))

        // verify
        assertAll(
            // 테스트 케이스 1회  + WebMvcTest가 기동되면서 ApplicationReadyEvent 발생으로 인한 추가 1회.
            { verify(blogSearchService, times(2)).refreshSearchBlogStrategyPriorities() }
        )
    }
}