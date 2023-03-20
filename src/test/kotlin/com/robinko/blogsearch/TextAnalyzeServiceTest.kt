package com.robinko.blogsearch

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.BDDMockito
import org.mockito.Captor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class TextAnalyzeServiceTest {

    @InjectMocks
    private lateinit var textAnalyzeService: TextAnalyzeService

    @Mock
    private lateinit var snsService: SnsService

    @Captor
    private lateinit var snsEventCaptor: ArgumentCaptor<SnsEvent>

    @Test
    fun extractKeywords() {
        // arrange
        val text = "몸과 마음을 지키는 레이몬드"

        // action
        val result = textAnalyzeService.extractKeywords(text)

        // verify
        assertAll(
            { Assertions.assertThat(result).isNotNull },
            { Assertions.assertThat(result).hasSameElementsAs(listOf("몸","마음", "레이몬드")) }
        )
    }

    @Test
    fun subscribeBlogSearchEvent() {
        // arrange
        val event = BlogSearchEvent("서울 명소")
        BDDMockito.willDoNothing().given(snsService).publish(nonNullAny(SnsEvent::class.java))

        // action
        textAnalyzeService.subscribeBlogSearchEvent(event)

        // verify
        assertAll(
            {
                Mockito.verify(snsService, Mockito.times(2)).publish(capture(snsEventCaptor))
            },
            {
                Assertions.assertThat(snsEventCaptor.allValues.first().eventName).isEqualTo("SearchCountUpdate")
            },
            {
                Assertions.assertThat(snsEventCaptor.allValues.first().id).isEqualTo("서울")
            },
            {
                Assertions.assertThat(snsEventCaptor.allValues.component2().eventName).isEqualTo("SearchCountUpdate")
            },
            {
                Assertions.assertThat(snsEventCaptor.allValues.component2().id).isEqualTo("명소")
            },
        )
    }
}