package com.robinko.blogsearch

import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.Id

@Suppress("unused")
@Entity
data class shedlock(
    @Id
    val name: String,
    val lock_until: LocalDateTime,
    val locked_at: LocalDateTime,
    val locked_by: String
)
