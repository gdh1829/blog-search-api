package com.robinko.blogsearch

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
class BlogSearchController(
    private val blogService: BlogService
) {
    @GetMapping("/blogs")
    fun searchBlog(
        @RequestParam query: String,
        @PageableDefault(page = 1, size = 20, sort = ["score"], direction = Sort.Direction.DESC)
        pageable: Pageable
    ): ResponseEntity<Page<BlogDoc>> {
        if (pageable.pageNumber < 1) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "PageNumber must start from 1.")
        }

        return blogService.searchBlog(query, pageable)
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.badRequest().build()
    }
}
