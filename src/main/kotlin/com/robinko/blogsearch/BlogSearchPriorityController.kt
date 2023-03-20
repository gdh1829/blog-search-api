package com.robinko.blogsearch

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class BlogSearchPriorityController(
    private val blogSearchService: BlogSearchService
) {

    @PutMapping("/blogSearchPriority", params = ["refresh=true"])
    fun applyBlogSearchPriority(): ResponseEntity<String> {
        blogSearchService.refreshSearchBlogStrategyPriorities()
        return ResponseEntity.ok("ok")
    }
}
