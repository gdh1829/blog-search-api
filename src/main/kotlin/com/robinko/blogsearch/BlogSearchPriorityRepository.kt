package com.robinko.blogsearch

import com.robinko.blogsearch.external.BlogSource
import org.springframework.data.jpa.repository.JpaRepository

interface BlogSearchPriorityRepository: JpaRepository<BlogSearchPriority, Long> {

    fun findBySource(source: BlogSource): BlogSearchPriority?
}
