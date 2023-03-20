package com.robinko.blogsearch

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class KeywordStatisticsController(
    private val keywordStatisticsService: KeywordStatisticsService,
) {

    @GetMapping("/keywordStatistics", params = ["top10=true"])
    fun getTop10Keywords(): List<KeywordStatistics> = keywordStatisticsService.findTop10Keywords()

    @DeleteMapping("/admin/keywordStatistics/{keyword}")
    fun deleteKeywordStatistics(
        @PathVariable keyword: String
    ): ResponseEntity<KeywordStatistics?> =
        keywordStatisticsService.deleteKeywordStatistics(keyword)
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
}
