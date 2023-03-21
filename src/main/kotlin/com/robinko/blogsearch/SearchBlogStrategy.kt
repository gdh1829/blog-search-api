package com.robinko.blogsearch

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

enum class BlogSource {
    KAKAO,
    NAVER
}
