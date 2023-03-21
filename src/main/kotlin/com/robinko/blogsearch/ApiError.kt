package com.robinko.blogsearch

import java.time.LocalDateTime

/**
 * Api 에러 응답 Dto.
 */
data class ApiError(
    val status: Int,
    val statusReason: String,
    val timestamp: LocalDateTime,
    val message: String,
)
