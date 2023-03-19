package com.robinko.blogsearch

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import javax.persistence.AttributeConverter

@Component
class SourceBlogStringConverter : AttributeConverter<SourceBlog, String> {

    private val log = LoggerFactory.getLogger(SourceBlogStringConverter::class.java)
    override fun convertToDatabaseColumn(attribute: SourceBlog?): String? {
        return attribute?.name
    }

    override fun convertToEntityAttribute(dbData: String?): SourceBlog? {
        return dbData
            ?.takeIf { it.isNotBlank() }
            ?.runCatching { SourceBlog.valueOf(this) }
            ?.onFailure { log.error("Failed to convert to entity attr: $dbData", it) }
            ?.getOrNull()
    }
}