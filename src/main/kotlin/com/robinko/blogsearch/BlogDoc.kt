package com.robinko.blogsearch

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.ZonedDateTime

/**
 * 카카오/네이버 등을 통하여 검색된 데이터를 클라이언트에게 전달하기 위한 표준화된 Dto.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class BlogDoc(
    val blogName: String,
    val title: String,
    val contents: String,
    val createdTime: ZonedDateTime
)
