package com.robinko.blogsearch

import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class KeywordStatisticsService(
    private val keywordStatisticsRepository: KeywordStatisticsRepository,
    private val eventPublisher: ApplicationEventPublisher
) {
    private val log = LoggerFactory.getLogger(KeywordStatisticsService::class.java)

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun updateSearchCount(keyword: String) {
        keywordStatisticsRepository.findByIdOrNull(keyword)
            ?.also {
                keywordStatisticsRepository.updateSearchCount(keyword)
                    .also { log.debug("KeywordStatistics search count updated: $it") }
            }
            ?: keywordStatisticsRepository.save(KeywordStatistics(keyword = keyword, searchCount = 1))
                .also { log.debug("KeywordStatistics newly saved: $it") }
    }

    @Transactional
    fun deleteKeywordStatistics(keyword: String) =
        keywordStatisticsRepository.findByIdOrNull(keyword)
            ?.also {
                keywordStatisticsRepository.delete(it)
                log.info("KeywordStatistics deleted: $it")
            }?.also { eventPublisher.publishEvent(KeywordStatisticsDeleteEvent(it)) }

    @Transactional(readOnly = true)
    @Cacheable(value = ["Top10Keywords"])
    fun getTop10Keywords(): List<KeywordStatistics> =
        keywordStatisticsRepository.findTop10ByOrderBySearchCountDescUpdatedTimeDesc()

    @Transactional(readOnly = true)
    @CachePut(value = ["Top10Keywords"])
    fun refreshTop10Keywords(): List<KeywordStatistics> =
        keywordStatisticsRepository.findTop10ByOrderBySearchCountDescUpdatedTimeDesc()
}
