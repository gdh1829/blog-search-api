package com.robinko.blogsearch

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.mockito.BDDMockito.given
import org.mockito.Mockito.verify
import org.mockito.Mockito.times
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(controllers = [KeywordStatisticsController::class])
class KeywordStatisticsControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var keywordStatisticsService: KeywordStatisticsService

    @Test
    fun `getTop10Keywords success`() {
        // arrange
        given(keywordStatisticsService.getTop10Keywords()).willReturn(emptyList())

        // action
        mockMvc.perform(
             get("/keywordStatistics")
                 .param("top10", "true")
                 .contentType(MediaType.APPLICATION_JSON)
                 .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())
             .andExpect(MockMvcResultMatchers.status().isOk)
             .andExpect(MockMvcResultMatchers.handler().methodName(KeywordStatisticsController::getTop10Keywords.name))

        // verify
        assertAll(
            { verify(keywordStatisticsService, times(1)).getTop10Keywords() },
        )
    }

    @Test
    fun `deleteKeywordStatistics success`() {
        // arrange
        val keyword = "apple"
        val keywordStatistics = KeywordStatistics(keyword)
        given(keywordStatisticsService.deleteKeywordStatistics(keyword)).willReturn(keywordStatistics)

        // action
        mockMvc.perform(
            delete("/admin/keywordStatistics/{keyword}", keyword)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(
                MockMvcResultMatchers.handler().methodName(KeywordStatisticsController::deleteKeywordStatistics.name)
            )

        // verify
        assertAll(
            {
                verify(keywordStatisticsService, times(1))
                    .deleteKeywordStatistics(keyword) 
            }
        )
    }

    @Test
    fun `deleteKeywordStatistics when delete target not found then return not found response`() {
        // arrange
        val keyword = "apple"

        // action
        mockMvc.perform(
            delete("/admin/keywordStatistics/{keyword}", keyword)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isNotFound)
            .andExpect(
                MockMvcResultMatchers.handler().methodName(KeywordStatisticsController::deleteKeywordStatistics.name)
            )

        // verify
        assertAll(
            {
                verify(keywordStatisticsService, times(1))
                    .deleteKeywordStatistics(keyword)
            }
        )
    }
}