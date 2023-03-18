package com.robinko.blogsearch

import com.robinko.blogsearch.kakao.KakaoSearchService
import com.robinko.blogsearch.naver.NaverSearchService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class BlogService(
    private val kakaoSearchService: KakaoSearchService,
    private val naverSearchService: NaverSearchService
) {
    fun searchBlog(
        query: String,
        pageable: Pageable
    ): Page<BlogDoc>? {
        return (
            kakaoSearchService.searchKakaoBlog(
                query, pageable.pageNumber, pageable.pageSize, pageable.sort.firstOrNull()?.property
            ) ?: naverSearchService.searchNaverBlog(
                query, pageable.pageNumber, pageable.pageSize, pageable.sort.firstOrNull()?.property
            )
            )?.toPage(pageable)
    }
}
