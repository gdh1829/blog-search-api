package com.robinko.blogsearch

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.ZonedDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
data class BlogDoc(
    val blogName: String,
    val title: String,
    val contents: String,
    val createdTime: ZonedDateTime
)
