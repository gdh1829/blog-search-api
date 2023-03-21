package com.robinko.blogsearch

import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.Id

/**
 * 스케줄러락을 위한 shedlock entity.
 */
@Suppress("unused")
@Entity
data class shedlock(
    @Id
    val name: String,
    val lock_until: LocalDateTime,
    val locked_at: LocalDateTime,
    val locked_by: String
)
