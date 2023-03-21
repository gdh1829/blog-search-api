package com.robinko.blogsearch

import java.io.Serializable

/**
 * localstack sns 서비스 메시지 발행 dto.
 */
data class SnsEvent(
    val eventName: String,
    val id: Serializable,
    val entityName: String,
    val entity: Any
)
