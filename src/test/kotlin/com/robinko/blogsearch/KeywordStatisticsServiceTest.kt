package com.robinko.blogsearch

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.BDDMockito
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.isA
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.junit.jupiter.MockitoExtension
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class KeywordStatisticsServiceTest {

    @InjectMocks
    private lateinit var keywordStatisticsService: KeywordStatisticsService

    @Mock
    private lateinit var keywordStatisticsRepository: KeywordStatisticsRepository

    @Test
    fun `updateSearchCount when previous exists then update count`() {
        // arrange
        val keyword = "apple"
        BDDMockito.given(keywordStatisticsRepository.findById(keyword))
            .willReturn(Optional.of(KeywordStatistics(keyword)))

        // action
        keywordStatisticsService.updateSearchCount(keyword)

        // verify
        assertAll(
            {
                verify(keywordStatisticsRepository, times(1))
                    .updateSearchCount(keyword)
            },
            {
                verifyNoMoreInteractions(keywordStatisticsRepository)
            }
        )
    }

    @Test
    fun `updateSearchCount when previous not exists then newly save`() {
        // arrange
        val keyword = "apple"
        val keywordStatistics = KeywordStatistics(keyword = keyword, searchCount = 1)

        BDDMockito.given(keywordStatisticsRepository.findById(keyword))
            .willReturn(Optional.ofNullable(null))
        BDDMockito.given(keywordStatisticsRepository.save(isA(KeywordStatistics::class.java)))
            .willReturn(keywordStatistics)

        // action
        keywordStatisticsService.updateSearchCount(keyword)

        // verify
        val captor = ArgumentCaptor.forClass(KeywordStatistics::class.java)
        assertAll(
            {
                verify(keywordStatisticsRepository, times(1))
                    .save(captor.capture())
            },
            { Assertions.assertThat(captor.value.keyword).isEqualTo(keyword) },
            { Assertions.assertThat(captor.value.searchCount).isEqualTo(1) },
            {
                verifyNoMoreInteractions(keywordStatisticsRepository)
            }
        )
    }

    @Test
    fun `deleteKeywordStatistics when found then delete`() {
        // arrange
        val keyword = "apple"
        BDDMockito.given(keywordStatisticsRepository.findById(keyword))
            .willReturn(Optional.of(KeywordStatistics(keyword)))

        // action
        keywordStatisticsService.deleteKeywordStatistics(keyword)

        // verify
        assertAll(
            {
                verify(keywordStatisticsRepository, times(1))
                    .delete(nonNullAny(KeywordStatistics::class.java))
            }
        )
    }

    @Test
    fun `deleteKeywordStatistics when not found then null`() {
        // arrange
        val keyword = "apple"
        BDDMockito.given(keywordStatisticsRepository.findById(keyword))
            .willReturn(Optional.ofNullable(null))

        // action
        keywordStatisticsService.deleteKeywordStatistics(keyword)

        // verify
        assertAll(
            {
                verify(keywordStatisticsRepository, times(0))
                    .delete(nonNullAny(KeywordStatistics::class.java))
            }
        )
    }

    @Test
    fun `findTop10Keywords`() {
        // arrange
        BDDMockito.given(keywordStatisticsRepository.findTop10ByOrderBySearchCountDescUpdatedTimeDesc())
            .willReturn(listOf(KeywordStatistics(keyword = "apple", searchCount = 1)))

        // action
        val result = keywordStatisticsService.findTop10Keywords()

        // verify
        assertAll(
            {
                Assertions.assertThat(result).isNotEmpty
            }
        )
    }
}
