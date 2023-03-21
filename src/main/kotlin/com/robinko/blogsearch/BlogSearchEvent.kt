package com.robinko.blogsearch

/**
 * 블로그 검색 발생 이벤트.
 * 내부적으로 해당 이벤트를 구독하여 키워드 집계 등과 같은 기타 부가적인 처리를 진행.
 */
data class BlogSearchEvent(
    val query: String
)
