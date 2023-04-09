package com.robinko.blogsearch.external

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.robinko.blogsearch.BlogDoc
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * 네이버 블로그 검색 결과 dto.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class NaverBlogSearchResult(
    val lastBuildDate: String,
    val total: Long,
    val start: Int,
    val display: Int,
    val items: List<NaverBlogDoc>
): ExternalSearchResult {

    override fun toPage(pageable: Pageable): Page<BlogDoc> = PageImpl(
        this.items.map { it.toBlogDoc() },
        PageRequest.of(pageable.pageSize, pageable.pageNumber, pageable.sort),
        this.total
    )
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class NaverBlogDoc(
    val title: String,
    val link: String,
    val description: String,
    @JsonAlias("bloggername")
    val bloggerName: String,
    @JsonAlias("bloggerlink")
    val bloggerLink: String,
    @JsonAlias("postdate")
    val postDate: String
): ExternalBlogDoc {

    override fun toBlogDoc(): BlogDoc = BlogDoc(
        title = this.title,
        blogName = this.bloggerName,
        contents = description,
        createdTime = LocalDate.from(DateTimeFormatter.ofPattern("yyyyMMdd", Locale.KOREA).parse(this.postDate))
            .atStartOfDay()
            .atZone(ZoneId.of("Asia/Seoul"))
    )
}
