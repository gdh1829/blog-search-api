package com.robinko.blogsearch

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
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

@Api("블로그 검색")
@RestController
class BlogSearchController(
    private val blogService: BlogService
) {
    @ApiOperation(
        "블로그 서치 API.",
        notes = "카카오/네이버 등의 블로그 소스로부터 query를 질의한 결과를 반환하며, BlogSearchEvent를 발생시킵니다."
    )
    @ApiResponses(
        ApiResponse(code = 200, message = "success"),
        ApiResponse(code = 400, message = "Bad request."),
        ApiResponse(code = 500, message = "Internal server error.")
    )
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
            ?: ResponseEntity.internalServerError().build()
    }
}
