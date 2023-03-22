package com.robinko.blogsearch

import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheEvict
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.event.TransactionalEventListener

@Service
class KeywordStatisticsEventHandlingService(
    private val keywordStatisticsService: KeywordStatisticsService
) {
    private val log = LoggerFactory.getLogger(KeywordStatisticsEventHandlingService::class.java)

    /**
     * 키워드 통계 삭제 커밋 이벤트 구독 핸들러.
     * 제거된 키워드가 Top10 키워드에 해당하면 캐싱 갱신을 진행.
     */
    @Async
    @TransactionalEventListener
    fun subscribeKeywordStatiticsDeleteEvent(event: KeywordStatisticsDeleteEvent) {
        val keyword = event.keywordStatistics.keyword

        if (keywordStatisticsService.getTop10Keywords().any { it.keyword == keyword }) {
            keywordStatisticsService.refreshTop10Keywords()
            log.debug("Top10Keywords cache evicted.")
        }
    }
}
