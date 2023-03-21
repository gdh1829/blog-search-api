package com.robinko.blogsearch

import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.ConcurrentLinkedDeque

@Transactional(readOnly = true)
@Service
class BlogSearchService(
    private val searchBlogStrategies: Set<SearchBlogStrategy>
) {
    private val log = LoggerFactory.getLogger(BlogSearchService::class.java)

    private var searchSources: ConcurrentLinkedDeque<SearchBlogStrategy> = ConcurrentLinkedDeque()

    fun searchBlog(query: String, page: Int, size: Int, sort: String?): ExternalSearchResult? {
        var searchResult: ExternalSearchResult? = null

        searchSources.takeWhile { strategy ->
            log.info("trying to search blog from ${strategy.getBlogSource()}")
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
    fun refreshSearchBlogStrategyPriorities(): List<BlogSource> {
        searchBlogStrategies.forEach { it.setBlogSearchPriority() }
        val sorted = searchBlogStrategies
            .filter { it.getBlogSearchPriority() != null && it.getBlogSearchPriority()!!.use }
            .sortedBy { it.getBlogSearchPriority()!!.priority }

        // 실시간으로 블로그 검색 기능속 다중 쓰레드 사이에서 참조되므로 갱신과정에서 동시성 문제가 일어나지 않도록 주의한다.
        val currentSize = searchSources.size
        // 뒤로부터 갱신되는 검색 우선 순위 데이터 삽입
        searchSources.addAll(sorted)
        // 앞에서부터 구 우선순위 데이터 개수만큼 제거
        (0 until currentSize).forEach { searchSources.pop() }

        val updated = searchSources.map { it.getBlogSource() }
        log.info("search source priority updated => $updated")

        return updated
    }
}
