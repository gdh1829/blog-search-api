package com.robinko.blogsearch

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class BlogSearchService(
    searchBlogStrategies: Set<SearchBlogStrategy>
) {
    private val log = LoggerFactory.getLogger(BlogSearchService::class.java)
    private val searchSources = searchBlogStrategies.associateBy { it.getSourceBlog() }

    fun searchBlog(query: String, page: Int, size: Int, sort: String?): ExternalSearchResult? {
        var searchResult: ExternalSearchResult? = null

        searchSources.entries.takeWhile { (sourceBlog, strategy) ->
            log.info("trying to search blog from $sourceBlog")
            strategy.searchBlog(query, page, size, sort)
                ?.also { searchResult = it }
                ?.let { false }
                ?: true
        }

        return searchResult
    }
}
