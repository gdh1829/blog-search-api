package com.robinko.blogsearch.external

import com.robinko.blogsearch.BlogDoc
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * 외부검색결과 인터페이스.
 */
interface ExternalSearchResult {
    /**
     * 외부 검색 결과를 Page 타입으로 변환.
     */
    fun toPage(pageable: Pageable): Page<BlogDoc>
}

