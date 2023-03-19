package com.robinko.blogsearch

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class KeywordController(
    private val keywordStatisticsService: KeywordStatisticsService
) {

    @GetMapping("/keywords", params = ["top10=true"])
    fun getTop10Keywords(): List<KeywordStatistics> = keywordStatisticsService.findTop10Keywords()
}
