package com.robinko.blogsearch

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.jdbc.core.JdbcTemplate
import java.net.http.HttpClient
import javax.sql.DataSource

@EnableCircuitBreaker
// @EnableJpaRepositories(basePackages = ["com.robinko.blogsearch"])
@EnableJpaRepositories
@EnableSchedulerLock(defaultLockAtLeastFor = "PT30S", defaultLockAtMostFor = "PT60S")
@Import(CommonConfig::class)
@Configuration
class ApplicationConfig {

    @Bean
    @Autowired
    fun lockProvider(dataSource: DataSource): JdbcTemplateLockProvider =
        JdbcTemplateLockProvider.Configuration.builder()
            .withJdbcTemplate(JdbcTemplate(dataSource))
            .usingDbTime()
            .build()
            .let { JdbcTemplateLockProvider(it) }

    @Bean
    fun httpClient(): HttpClient = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_1_1)
        .build()

    @Bean
    fun om(): ObjectMapper = jacksonObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        .registerModules(JavaTimeModule(), Jdk8Module())
}