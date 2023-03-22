package com.robinko.blogsearch

import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class KeywordStatisticsController(
    private val keywordStatisticsService: KeywordStatisticsService,
) {

    @ApiOperation("상위 10개의 핫키워드 조회 API.")
    @ApiResponses(
        ApiResponse(code = 200, message = "success")
    )
    @GetMapping("/keywordStatistics", params = ["top10=true"])
    fun getTop10Keywords(): List<KeywordStatistics> = keywordStatisticsService.getTop10Keywords()

    @ApiOperation("키워드 통계 제거 API.")
    @ApiResponses(
        ApiResponse(code = 200, message = "success"),
        ApiResponse(code = 404, message = "Resource Not found")
    )
    @DeleteMapping("/admin/keywordStatistics/{keyword}")
    fun deleteKeywordStatistics(
        @PathVariable keyword: String
    ): ResponseEntity<KeywordStatistics?> =
        keywordStatisticsService.deleteKeywordStatistics(keyword)
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
}
