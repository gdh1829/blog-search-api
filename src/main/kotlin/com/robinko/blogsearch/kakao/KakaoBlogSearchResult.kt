package com.robinko.blogsearch.kakao

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.robinko.blogsearch.BlogDoc
import com.robinko.blogsearch.ExternalBlogDoc
import com.robinko.blogsearch.ExternalSearchResult
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import java.time.ZonedDateTime

/**
 * 카카오 블로그 서치 결과 dto.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class KakaoBlogSearchResult(
    val meta: SearchMeta,
    val documents: List<KakaoBlogDoc>
): ExternalSearchResult {
    override fun toPage(pageable: Pageable): Page<BlogDoc> = PageImpl(
        this.documents.map { it.toBlogDoc() },
        PageRequest.of(pageable.pageSize, pageable.pageNumber, pageable.sort),
        this.meta.totalCount
    )
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class SearchMeta(
    @JsonAlias("total_count")
    val totalCount: Long,
    @JsonAlias("pageable_count")
    val pageableCount: Long,
    @JsonAlias("is_end")
    val isEnd: Boolean,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class KakaoBlogDoc(
    @JsonAlias("blogname")
    val blogName: String,
    val contents: String,
    val datetime: ZonedDateTime,
    val thumbnail: String?,
    val title: String,
    val url: String?
): ExternalBlogDoc {

    override fun toBlogDoc(): BlogDoc = BlogDoc(
        blogName = this.blogName,
        title = this.title,
        contents = this.contents,
        createdTime = this.datetime
    )
}
