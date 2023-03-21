package com.robinko.blogsearch

import org.springframework.data.jpa.repository.JpaRepository

interface BlogSearchPriorityRepository: JpaRepository<BlogSearchPriority, Long> {

    fun findBySource(source: BlogSource): BlogSearchPriority?
}
