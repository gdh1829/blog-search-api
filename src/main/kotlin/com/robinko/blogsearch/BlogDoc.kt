package com.robinko.blogsearch

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import java.time.ZonedDateTime

/**
 * 카카오/네이버 등을 통하여 검색된 데이터를 클라이언트에게 전달하기 위한 표준화된 Dto.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel
data class BlogDoc(
    @ApiModelProperty(value = "블로그 이름", example = "러빙데이, 소소한 맛집 공유")
    val blogName: String,
    @ApiModelProperty(value = "제목", example = "남양주 <b>브런치</b>맛집 대너리스 팔당 카페")
    val title: String,
    @ApiModelProperty(value = "내용", example = "유명한 &#34;대너리스&#34; 에요!...")
    val contents: String,
    @ApiModelProperty(value = "포스팅 시간", example = "2023-03-09T01:21:22Z")
    val createdTime: ZonedDateTime
)
