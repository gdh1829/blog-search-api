package com.robinko.blogsearch

import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL
import kr.co.shineware.nlp.komoran.core.Komoran
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class TextAnalyzeService(
    private val snsService: SnsService
) {
    private val log = LoggerFactory.getLogger(TextAnalyzeService::class.java)
    private val komoranAnalyzer = Komoran(DEFAULT_MODEL.LIGHT)

    fun extractKeywords(text: String): List<String>? {
        return kotlin.runCatching { komoranAnalyzer.analyze(text).nouns }
            .onSuccess { log.debug("text analyzed => $it") }
            .onFailure { log.warn("failed to analyze text: $text") }
            .getOrNull()
    }

    @Async
    @EventListener
    fun subscribeBlogSearchEvent(event: BlogSearchEvent) {
        extractKeywords(event.query)
            ?.filter { it.isNotBlank() }
            ?.forEach {
                snsService.publish(
                    SnsEvent(
                        eventName = "SearchCountUpdate",
                        id = it,
                        entityName = KeywordStatistics::class.java.simpleName,
                        entity = KeywordStatistics(keyword = it)
                    )
                )
            }
    }
}