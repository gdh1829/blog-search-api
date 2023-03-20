package com.robinko.blogsearch

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BlogSearchApiApplication

fun main(args: Array<String>) {
	runApplication<BlogSearchApiApplication>(*args)
}
