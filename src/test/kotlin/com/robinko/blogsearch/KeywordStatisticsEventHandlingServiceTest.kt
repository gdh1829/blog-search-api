package com.robinko.blogsearch

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class KeywordStatisticsEventHandlingServiceTest {

    @InjectMocks
    private lateinit var keywordStatisticsEventHandlingService: KeywordStatisticsEventHandlingService

    @Mock
    private lateinit var keywordStatisticsService: KeywordStatisticsService

    @Test
    fun `subscribeKeywordStatiticsDeleteEvent when event keyword belong to top10s then refresh`() {
        // arrange
        val keywordStatistics = KeywordStatistics(keyword = "apple")
        BDDMockito.given(keywordStatisticsService.getTop10Keywords())
            .willReturn(listOf(keywordStatistics))

        // action
        keywordStatisticsEventHandlingService
            .subscribeKeywordStatiticsDeleteEvent(KeywordStatisticsDeleteEvent(keywordStatistics))

        // verify
        verify(keywordStatisticsService, times(1)).refreshTop10Keywords()
    }

    @Test
    fun `subscribeKeywordStatiticsDeleteEvent when event keyword not belong to top10s then do nothing`() {
        // arrange
        val keywordStatistics = KeywordStatistics(keyword = "apple")
        val eventKeywordStatistics = KeywordStatistics(keyword = "apple22")
        BDDMockito.given(keywordStatisticsService.getTop10Keywords())
            .willReturn(listOf(keywordStatistics))

        // action
        keywordStatisticsEventHandlingService
            .subscribeKeywordStatiticsDeleteEvent(KeywordStatisticsDeleteEvent(eventKeywordStatistics))

        // verify
        verify(keywordStatisticsService, times(0)).refreshTop10Keywords()
    }
}
