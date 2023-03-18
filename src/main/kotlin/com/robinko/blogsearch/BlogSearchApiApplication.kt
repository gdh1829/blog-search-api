package com.robinko.blogsearch

import com.robinko.blogsearch.config.CommonConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@SpringBootApplication
@Import(CommonConfig::class)
class BlogSearchApiApplication

fun main(args: Array<String>) {
	runApplication<BlogSearchApiApplication>(*args)
}
