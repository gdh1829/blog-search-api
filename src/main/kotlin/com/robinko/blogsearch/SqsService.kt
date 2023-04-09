package com.robinko.blogsearch

import com.amazonaws.services.sqs.AmazonSQSAsync
import com.amazonaws.services.sqs.model.DeleteMessageBatchRequestEntry
import com.amazonaws.services.sqs.model.Message
import com.amazonaws.services.sqs.model.ReceiveMessageRequest
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import javax.annotation.PreDestroy

@Service
@Profile("sqs")
class SqsService(
    @Value("\${localstack.sqs.queueUrl}")
    private val queueUrl: String,
    private val keywordStatisticsService: KeywordStatisticsService,
    private val sqs: AmazonSQSAsync
) {

    private val log = LoggerFactory.getLogger(SqsService::class.java)

    private val om: ObjectMapper = jacksonObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        .registerModules(JavaTimeModule(), Jdk8Module())

    @PreDestroy
    fun destroy() {
        sqs.shutdown()
    }

    @Scheduled(initialDelay = 60_000, fixedDelay = 1_000)
    fun receiveMessage() {
        val request = ReceiveMessageRequest()
            .withQueueUrl(queueUrl)
            .withVisibilityTimeout(60)
            .withMaxNumberOfMessages(10)

        val messages = sqs.receiveMessage(request).messages
        if (messages.isEmpty()) {
            Thread.sleep(5_000L)
            return
        }

        messages.mapNotNull { message ->
            if (message?.body.isNullOrBlank()) return@mapNotNull message

            val event = om.readValueOrNull(message.body, SnsEvent::class.java) ?: return@mapNotNull message

            // 예외 발생시 큐 메시지는 삭제하지 않고 sqs에게 반환되고 다시 재처리될 수 있도록 한다.
            kotlin.runCatching {
                when (event.entityName) {
                    KeywordStatistics::class.java.simpleName -> consumeKeywordStatisticsMessage(event)
                    else -> log.debug("else message: {}", message)
                }
                message
            }.getOrNull()
        }.run { deleteSqsMessageBatchAsync(this, request) }
    }

    internal fun consumeKeywordStatisticsMessage(event: SnsEvent) {
        log.info("Received Message: $event")

        val entity = om.convertValueOrNull(event.entity, KeywordStatistics::class.java)
            ?: return

        when (event.eventName) {
            "SearchCountUpdate" -> keywordStatisticsService.updateSearchCount(entity.keyword)
            else -> log.debug("Ignored. Unknown event: $event")
        }
    }

    fun deleteSqsMessageBatchAsync(messages: List<Message>, request: ReceiveMessageRequest) {
        val deleteRequests = messages.takeIf { it.isNotEmpty() }
            ?.map { DeleteMessageBatchRequestEntry(it.messageId, it.receiptHandle) }
            ?: return

        sqs.deleteMessageBatchAsync(queueUrl, deleteRequests)
            .runCatching { log.debug("Messages deleted : {} -> {}", request, this.get()?.sdkResponseMetadata) }
            .onFailure { log.error("Failed to delete AWS SQS message.", it) }
    }
}
