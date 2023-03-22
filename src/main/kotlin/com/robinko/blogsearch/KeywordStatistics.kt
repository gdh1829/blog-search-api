package com.robinko.blogsearch

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Where
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

/**
 * 유저 검색 쿼리를 표준화한 키워드로 조회수 집계 Entity.
 */
@ApiModel
@Where(clause = "deletedTime is null")
@SQLDelete(sql = "UPDATE KeywordStatistics SET deletedTime = now() WHERE keyword = ?")
@Entity
data class KeywordStatistics(
    @ApiModelProperty(value = "키워드")
    @Id
    val keyword: String,

    @ApiModelProperty(value = "데이터 생성시각")
    @CreatedDate
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(nullable = false, updatable = false)
    val createdTime: LocalDateTime = LocalDateTime.now(),

    @ApiModelProperty(value = "데이터 최종 수정시각")
    @LastModifiedDate
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(nullable = false)
    val updatedTime: LocalDateTime = LocalDateTime.now(),

    @ApiModelProperty(value = "데이터 삭제 시각")
    val deletedTime: LocalDateTime? = null,

    @ApiModelProperty(value = "키워드 검색 카운트")
    val searchCount: Long = 1
)
