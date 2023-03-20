package com.robinko.blogsearch

import java.time.LocalDateTime

data class ApiError(
    val status: Int,
    val statusReason: String,
    val timestamp: LocalDateTime,
    val message: String,
)
