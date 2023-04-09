package com.robinko.blogsearch

import com.robinko.blogsearch.external.BlogSource
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Version

/**
 * 외부 연동 블로그 검색 서비스(카카오/네이버 등)에 대한 검색 우선 순위 관리 entity.
 */
@ApiModel
@Entity
data class BlogSearchPriority(
    @ApiModelProperty(value = "서버ID", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val serverId: Long,

    @ApiModelProperty(value = "데이터 버전", example = "0")
    @Version
    var version: Long = 0,

    @ApiModelProperty(value = "데이터 생성시각")
    @CreatedDate
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(updatable = false)
    val createdTime: LocalDateTime = LocalDateTime.now(),

    @ApiModelProperty(value = "데이터 갱신 시간")
    @LastModifiedDate
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    val updatedTime: LocalDateTime = LocalDateTime.now(),

    @ApiModelProperty(value = "블로그검색소스", example = "KAKAO")
    @Column(unique = true)
    @Enumerated(value = EnumType.STRING)
    val source: BlogSource,

    @ApiModelProperty(value = "사용여부", example = "true")
    val use: Boolean = false,

    @ApiModelProperty(value = "우선순위", example = "0")
    val priority: Int
)
