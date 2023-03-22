package com.robinko.blogsearch

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.mockito.BDDMockito.given
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort.Direction
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(controllers = [BlogSearchPriorityController::class])
class BlogSearchPriorityControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var blogSearchService: BlogSearchService

    @MockBean
    private lateinit var blogSearchPriorityRepository: BlogSearchPriorityRepository

    private val om: ObjectMapper = jacksonObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        .registerModules(JavaTimeModule(), Jdk8Module())

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
            { verify(blogSearchService, times(1)).refreshSearchBlogStrategyPriorities() }
        )
    }

    @Test
    fun `getPagedBlogSearchPriorities success`() {
        // arrange
        val pageable = PageRequest.of(0, 10, Direction.ASC, "priority")
        val list = listOf(
            BlogSearchPriority(serverId = 1, source = BlogSource.NAVER, priority = 5),
            BlogSearchPriority(serverId = 2, source = BlogSource.KAKAO, priority = 10)
        )
        val paged = PageImpl(list, pageable, 2)
        given(blogSearchPriorityRepository.findAll(nonNullAny(Pageable::class.java))).willReturn(paged)

        // action
        mockMvc.perform(
            get("/blogSearchPriorities")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(
                MockMvcResultMatchers.handler().methodName(BlogSearchPriorityController::getPagedBlogSearchPriorities.name)
            )
            .andExpect(MockMvcResultMatchers.content().string(om.writeValueAsString(paged)))

        // verify
        assertAll(
            {
                verify(blogSearchPriorityRepository, times(1)).findAll(pageable)
            }
        )
    }

    @Test
    fun `getPagedBlogSearchPriorities with not allowed sort param values then bad request`() {
        // arrange

        // action
        mockMvc.perform(
            get("/blogSearchPriorities")
                .param("sort", "abc")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(
                MockMvcResultMatchers.handler().methodName(BlogSearchPriorityController::getPagedBlogSearchPriorities.name)
            )

        // verify
        assertAll(
            {
                verify(blogSearchPriorityRepository, times(0))
                    .findAll(nonNullAny(Pageable::class.java))
            }
        )
    }
}