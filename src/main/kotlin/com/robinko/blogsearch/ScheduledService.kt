package com.robinko.blogsearch

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Profile("scheduled")
@Service
class ScheduledService(
    private val keywordStatisticsService: KeywordStatisticsService
) {

    /**
     * 5분 단위로 상위 10 인기 키워드 캐시를 갱신 스케줄러.
     */
    @Scheduled(cron = "*/5 * * * * *", zone = "Asia/Seoul")
    @SchedulerLock(name = "refreshCacheTop10Keywords", lockAtMostFor = "PT10M")
    fun refreshCacheTop10Keywords() {
        keywordStatisticsService.refreshTop10Keywords()
    }
}
