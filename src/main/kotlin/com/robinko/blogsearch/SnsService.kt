package com.robinko.blogsearch

import com.amazonaws.services.sns.AmazonSNS
import com.amazonaws.services.sns.model.PublishRequest
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import javax.annotation.PreDestroy

@Service
class SnsService(
    @Value("\${localstack.sns.topicPrefix:}")
    private val snsTopicPrefix: String,
    private val sns: AmazonSNS
) {
    private val log = LoggerFactory.getLogger(SnsService::class.java)

    private val om = jacksonObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        .registerModules(JavaTimeModule(), Jdk8Module())

    @PreDestroy
    fun destroy() {
        sns.shutdown()
    }

    @Async
    fun publish(snsEvent: SnsEvent) {
        PublishRequest("$snsTopicPrefix${snsEvent.entityName}", om.writeValueAsString(snsEvent))
            .let { sns.publish(it) }
            .also { log.info("AWS SNS $snsTopicPrefix publish result: $it") }
    }
}