package com.robinko.blogsearch

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.verifyNoInteractions
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(controllers = [BlogSearchController::class])
class BlogSearchControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var blogService: BlogService

    @Test
    fun `searchBlog success`() {
        // arrange
        val query = "kotiln"
        val pageable = PageRequest.of(1, 20, Sort.Direction.DESC, "score")
        given(blogService.searchBlog(query, pageable)).willReturn(Page.empty())

        // action
         mockMvc.perform(
             get("/blogs")
                 .param("query", query)
                 .contentType(MediaType.APPLICATION_JSON)
                 .accept(MediaType.APPLICATION_JSON)
         ).andDo(MockMvcResultHandlers.print())
             .andExpect(MockMvcResultMatchers.status().isOk)
             .andExpect(MockMvcResultMatchers.handler().methodName(BlogSearchController::searchBlog.name))

        // verify
        assertAll(
            { Mockito.verify(blogService, Mockito.times(1)).searchBlog(query, pageable) }
        )
    }

    @Test
    fun `searchBlog when blog not found then bad request`() {
        // arrange
        val query = "kotiln"
        val pageable = PageRequest.of(1, 20, Sort.Direction.DESC, "score")

        // action
        mockMvc.perform(
            get("/blogs")
                .param("query", query)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isInternalServerError)
            .andExpect(MockMvcResultMatchers.handler().methodName(BlogSearchController::searchBlog.name))

        // verify
        assertAll(
            { Mockito.verify(blogService, Mockito.times(1)).searchBlog(query, pageable) }
        )
    }

    @Test
    fun `searchBlog when page not started from 1 then bad request`() {
        // arrange
        val query = "kotiln"

        // action
        mockMvc.perform(
            get("/blogs")
                .param("query", query)
                .param("page", "0")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.handler().methodName(BlogSearchController::searchBlog.name))

        // verify
        assertAll(
            { verifyNoInteractions(blogService) }
        )
    }

    @Test
    fun `searchBlog with invalid sort param then bad request`() {
        // arrange
        val query = "kotiln"

        // action
        mockMvc.perform(
            get("/blogs")
                .param("query", query)
                .param("sort", "abc")
                .param("page", "0")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.handler().methodName(BlogSearchController::searchBlog.name))

        // verify
        assertAll(
            { verifyNoInteractions(blogService) }
        )
    }
}