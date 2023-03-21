package com.robinko.blogsearch

import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

@Transactional(readOnly = true)
@Service
class BlogSearchService(
    private val searchBlogStrategies: Set<SearchBlogStrategy>
) {
    private val log = LoggerFactory.getLogger(BlogSearchService::class.java)

    private var searchSources: ConcurrentMap<Int, SearchBlogStrategy> = ConcurrentHashMap()

    fun searchBlog(query: String, page: Int, size: Int, sort: String?): ExternalSearchResult? {
        var searchResult: ExternalSearchResult? = null

        searchSources.values.takeWhile { strategy ->
            log.info("trying to search blog from ${strategy.blogSource}")
            strategy.searchBlog(query, page, size, sort)
                ?.also { searchResult = it }
                ?.let { false }
                ?: true
        }

        return searchResult
    }

    /**
     * 애플리케이션 초기 기동 준비 완료시, 검색소스 우선 순위 데이터 로드.
     */
    @EventListener(ApplicationReadyEvent::class)
    fun refreshSearchBlogStrategyPriorities() {
        searchBlogStrategies.forEach { it.setBlogSearchPriority() }
        searchSources.putAll(searchBlogStrategies
            .filter { it.getBlogSearchPriority() != null && it.getBlogSearchPriority()!!.use }
            .associateBy { it.getBlogSearchPriority()!!.priority }
            .toSortedMap(Comparator.naturalOrder()))
    }
}
