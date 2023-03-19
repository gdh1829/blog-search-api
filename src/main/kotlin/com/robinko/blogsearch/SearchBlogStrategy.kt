package com.robinko.blogsearch

interface SearchBlogStrategy {

    fun getSourceBlog(): SourceBlog

    fun searchBlog(
        query: String,
        page: Int = 1,
        size: Int = 20,
        sort: String? = null
    ): ExternalSearchResult?
}

enum class SourceBlog {
    KAKAO,
    NAVER
}
