package com.robinko.blogsearch

import java.io.Serializable

data class SnsEvent(
    val eventName: String,
    val id: Serializable,
    val entityName: String,
    val entity: Any
)
