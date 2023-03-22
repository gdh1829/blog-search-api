package com.robinko.blogsearch

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RestController

@Api("블로그 검색 소스 우선순위 API")
@RestController
class BlogSearchPriorityController(
    private val blogSearchPriorityRepository: BlogSearchPriorityRepository,
    private val blogSearchService: BlogSearchService
) {

    @ApiOperation("외부 연동 블로그 검색 소스 우선순위 데이터 조회 API. 우선순위 오름차순.")
    @ApiResponses(
        ApiResponse(code = 200, message = "success")
    )
    @GetMapping("/blogSearchPriorities")
    fun getPagedBlogSearchPriorities(
        @PageableDefault(page = 0, size = 10, sort = ["priority"], direction = Sort.Direction.ASC)
        pageable: Pageable
    ): ResponseEntity<Page<BlogSearchPriority>> {
        return blogSearchPriorityRepository.findAll(pageable)
            .let { ResponseEntity.ok(it) }
    }

    @ApiOperation("외부 연동 블로그 검색 소스 우선순위 변동 데이터 적용 API.")
    @ApiResponses(
        ApiResponse(code = 200, message = "success")
    )
    @PutMapping("/blogSearchPriority", params = ["refresh=true"])
    fun applyBlogSearchPriority(): ResponseEntity<List<BlogSource>> {
        return blogSearchService.refreshSearchBlogStrategyPriorities()
            .let { ResponseEntity.ok(it) }
    }
}
