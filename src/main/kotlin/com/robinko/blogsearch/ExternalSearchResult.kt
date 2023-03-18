package com.robinko.blogsearch

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ExternalSearchResult {
    fun toPage(pageable: Pageable): Page<BlogDoc>
}

