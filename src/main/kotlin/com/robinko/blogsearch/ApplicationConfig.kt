package com.robinko.blogsearch

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.sns.AmazonSNS
import com.amazonaws.services.sns.AmazonSNSClientBuilder
import com.amazonaws.services.sqs.AmazonSQSAsync
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.jdbc.core.JdbcTemplate
import java.net.http.HttpClient
import javax.sql.DataSource

@EnableCircuitBreaker
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
    fun sns(
        @Value("\${localstack.endpoint:}") endpoint: String,
        @Value("\${localstack.region:}") region: String,
    ): AmazonSNS = AmazonSNSClientBuilder.standard()
        .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration(endpoint, region))
        // set dummy credentials to avoid sdk error. Localstack does not have IAM authentication mechanism
        .withCredentials(AWSStaticCredentialsProvider(BasicAWSCredentials("default", "default")))
        .build()

    @Bean
    fun sqs(
        @Value("\${localstack.endpoint}") endpoint: String,
        @Value("\${localstack.region:}") region: String,
    ): AmazonSQSAsync = AmazonSQSAsyncClientBuilder.standard()
        .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration(endpoint, region))
        // set dummy credentials to avoid sdk error. Localstack does not have IAM authentication mechanism
        .withCredentials(AWSStaticCredentialsProvider(BasicAWSCredentials("default", "default")))
        .build()
}