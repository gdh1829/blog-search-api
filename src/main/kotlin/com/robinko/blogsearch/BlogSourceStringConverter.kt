package com.robinko.blogsearch

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import javax.persistence.AttributeConverter

@Component
class BlogSourceStringConverter : AttributeConverter<BlogSource, String> {

    private val log = LoggerFactory.getLogger(BlogSourceStringConverter::class.java)
    override fun convertToDatabaseColumn(attribute: BlogSource?): String? {
        return attribute?.name
    }

    override fun convertToEntityAttribute(dbData: String?): BlogSource? {
        return dbData
            ?.takeIf { it.isNotBlank() }
            ?.runCatching { BlogSource.valueOf(this) }
            ?.onFailure { log.error("Failed to convert to entity attr: $dbData", it) }
            ?.getOrNull()
    }
}