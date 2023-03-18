package com.robinko.blogsearch

import org.assertj.core.api.Assertions
import org.jasypt.encryption.StringEncryptor
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class BlogSearchApiApplicationIT {

	@Autowired
	lateinit var stringEncryptor: StringEncryptor

	private val log = LoggerFactory.getLogger(BlogSearchApiApplicationIT::class.java)

	@DisplayName("Encryption 생성기")
	@Test
	fun encrypt() {
		val sample = "5bjiWL671L"
		val encrypted = stringEncryptor.encrypt(sample)
		log.info("$sample encrypted => $encrypted")

		assertAll(
			{ Assertions.assertThat(encrypted).isNotNull },
			{ Assertions.assertThat(encrypted).isNotBlank() }
		)
	}
}
