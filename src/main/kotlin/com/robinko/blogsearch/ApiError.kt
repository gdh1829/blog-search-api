package com.robinko.blogsearch

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import java.time.LocalDateTime

/**
 * Api 에러 응답 Dto.
 */
@ApiModel
data class ApiError(
    @ApiModelProperty(value = "Http 상태코드", example = "400")
    val status: Int,
    @ApiModelProperty(value = "Http 상태코드 사유", example = "Bad Request")
    val statusReason: String,
    @ApiModelProperty(value = "에러 시각", example = "2023-03-22T11:24:02.546364")
    val timestamp: LocalDateTime,
    @ApiModelProperty(value = "에러 메시지", example = "PageNumber must start from 1.")
    val message: String,
)
