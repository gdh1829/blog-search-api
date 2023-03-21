package com.robinko.blogsearch

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class BlogSearchPriorityController(
    private val blogSearchService: BlogSearchService
) {

    /**
     * 외부 연동 블로그 검색 소스 우선순위 리프레쉬 API.
     */
    @PutMapping("/blogSearchPriority", params = ["refresh=true"])
    fun applyBlogSearchPriority(): ResponseEntity<List<BlogSource>> {
        return blogSearchService.refreshSearchBlogStrategyPriorities()
            .let { ResponseEntity.ok(it) }
    }
}
