package com.robinko.blogsearch

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Convert
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Version

@Entity
data class BlogSearchPriority(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val serverId: Long,

    @Version
    var version: Long = 0,

    @CreatedDate
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(updatable = false)
    val createdTime: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    val updatedTime: LocalDateTime = LocalDateTime.now(),

    @Convert(converter = SourceBlogStringConverter::class)
    @Column(unique = true)
    val source: SourceBlog,

    val use: Boolean = false,

    val priority: Int
)
