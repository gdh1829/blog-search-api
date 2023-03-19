package com.robinko.blogsearch

import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Where
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Where(clause = "deletedTime is null")
@SQLDelete(sql = "UPDATE KeywordStatistics SET deletedTime = now() WHERE keyword = ?")
@Entity
data class KeywordStatistics(
    @Id
    val keyword: String,

    @CreatedDate
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(nullable = false, updatable = false)
    val createdTime: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(nullable = false)
    val updatedTime: LocalDateTime = LocalDateTime.now(),

    val deletedTime: LocalDateTime? = null,

    val searchCount: Long = 1
)
