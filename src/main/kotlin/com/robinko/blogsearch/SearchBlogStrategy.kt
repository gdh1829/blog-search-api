package com.robinko.blogsearch

/**
 * 카카오 외에 네이버 등의 외부 연동 블로그 검색 전략 인터페이스.
 */
interface SearchBlogStrategy {
    val blogSource: BlogSource

    fun getBlogSearchPriority(): BlogSearchPriority?

    fun setBlogSearchPriority()

    fun searchBlog(
        query: String,
        page: Int = 1,
        size: Int = 20,
        sort: String? = null
    ): ExternalSearchResult?
}

/**
 * 외부 연동 블로그 소스.
 * 카카오, 네이버 지원 중.
 */
enum class BlogSource {
    KAKAO,
    NAVER
}
