package com.robinko.blogsearch

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class TextAnalyzeServiceTest {

    @InjectMocks
    private lateinit var textAnalyzeService: TextAnalyzeService

    @Mock
    private lateinit var snsService: SnsService

    @Test
    fun extractKeywords() {
        val text = "몸과 마음을 지키는 레이몬드"
        val result = textAnalyzeService.extractKeywords(text)

        assertAll(
            { Assertions.assertThat(result).isNotNull },
            { Assertions.assertThat(result).hasSameElementsAs(listOf("몸","마음", "레이몬드")) }
        )
    }
}