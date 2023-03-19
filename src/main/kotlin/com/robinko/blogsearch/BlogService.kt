package com.robinko.blogsearch

import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class BlogService(
    private val blogSearchService: BlogSearchService,
    private val eventPublisher: ApplicationEventPublisher
) {
    fun searchBlog(
        query: String,
        pageable: Pageable
    ): Page<BlogDoc>? =
        blogSearchService.searchBlog(
            query,
            pageable.pageNumber,
            pageable.pageSize,
            pageable.sort.firstOrNull()?.property
        )
        ?.toPage(pageable)
        ?.also { eventPublisher.publishEvent(BlogSearchEvent(query)) }
}
