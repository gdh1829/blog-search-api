package com.robinko.blogsearch

import com.amazonaws.services.sqs.AmazonSQSAsync
import com.amazonaws.services.sqs.model.DeleteMessageBatchResult
import com.amazonaws.services.sqs.model.Message
import com.amazonaws.services.sqs.model.ReceiveMessageRequest
import com.amazonaws.services.sqs.model.ReceiveMessageResult
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.anyList
import org.mockito.BDDMockito.anyString
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.mock
import org.mockito.BDDMockito.times
import org.mockito.BDDMockito.verify
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import java.util.concurrent.CompletableFuture

@ExtendWith(MockitoExtension::class)
class SqsServiceTest {

    private lateinit var sqsService: SqsService

    @Mock
    private lateinit var sqs: AmazonSQSAsync
    @Mock
    private lateinit var keywordStatisticsService: KeywordStatisticsService

    private val om: ObjectMapper = jacksonObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        .registerModules(JavaTimeModule(), Jdk8Module())

    @BeforeEach
    fun init() {
        sqsService = SqsService("http://localhost", keywordStatisticsService, sqs)
    }

    @Test
    fun destroy() {
        sqsService.destroy()
    }

    @Test
    fun `receiveMessage when empty messages then do nothing`() {
        // arrange
        val messagesStub = mock(ReceiveMessageResult::class.java).apply {
            given(this.messages).willReturn(emptyList())
        }
        given(sqs.receiveMessage(nonNullAny(ReceiveMessageRequest::class.java)))
            .willReturn(messagesStub)

        // action & verify
        assertDoesNotThrow {
            sqsService.receiveMessage()
        }
    }

    @Test
    fun `receiveMessage when body is null`() {
        // arrange
        val messageListStub = listOf(mock(Message::class.java))
        val ReceiveMessageResultStub = mock(ReceiveMessageResult::class.java)
            .apply { given(this.messages).willReturn(messageListStub) }
        given(sqs.receiveMessage(nonNullAny(ReceiveMessageRequest::class.java)))
            .willReturn(ReceiveMessageResultStub)

        // action
        assertDoesNotThrow { sqsService.receiveMessage() }

        // verify
        verify(sqs, times(1)).deleteMessageBatchAsync(anyString(), anyList())
    }

    @Test
    fun `receiveMessage when failed to parse message body`() {
        // arrange
        val messageListStub = listOf(
            mock(Message::class.java).apply {
                given(body).willReturn("{xxxxx}")
            }
        )
        val ReceiveMessageResultStub = mock(ReceiveMessageResult::class.java).apply {
            given(this.messages).willReturn(messageListStub)
        }
        given(sqs.receiveMessage(nonNullAny(ReceiveMessageRequest::class.java))).willReturn(ReceiveMessageResultStub)

        // action
        assertDoesNotThrow { sqsService.receiveMessage() }

        // verify
        verify(sqs, times(1)).deleteMessageBatchAsync(anyString(), anyList())
    }

    @Test
    fun `receiveMessage with unknown entity name then ignored`() {
        // arrange
        val messageListStub = listOf(
            mock(Message::class.java).apply {
                given(body)
                    .willReturn(
                        om.writeValueAsString(SnsEvent("unknown", 0, "unknown", ""))
                    )
            }
        )
        val ReceiveMessageResultStub = mock(ReceiveMessageResult::class.java).apply {
            given(this.messages).willReturn(messageListStub)
        }
        given(sqs.receiveMessage(nonNullAny(ReceiveMessageRequest::class.java))).willReturn(ReceiveMessageResultStub)

        // action
        assertDoesNotThrow { sqsService.receiveMessage() }

        // verify
        verify(sqs, times(1)).deleteMessageBatchAsync(anyString(), anyList())
    }

    @Test
    fun `receiveMessage with KeywordStatistics entity name then updateSearchCount`() {
        // arrange
        val messageListStub = listOf(
            mock(Message::class.java).apply {
                given(body).willReturn(
                    om.writeValueAsString(
                        SnsEvent(
                            "SearchCountUpdate",
                            "apple",
                            KeywordStatistics::class.java.simpleName,
                            KeywordStatistics(keyword = "apple")
                        )
                    )
                )
            }
        )
        val ReceiveMessageResultStub = mock(ReceiveMessageResult::class.java).apply {
            given(this.messages).willReturn(messageListStub)
        }
        given(sqs.receiveMessage(nonNullAny(ReceiveMessageRequest::class.java))).willReturn(ReceiveMessageResultStub)
        given(sqs.deleteMessageBatchAsync(anyString(), anyList()))
            .willReturn(CompletableFuture.completedFuture(mock(DeleteMessageBatchResult::class.java)))

        // Action
        assertDoesNotThrow { sqsService.receiveMessage() }

        // verify
        verify(keywordStatisticsService, times(1)).updateSearchCount("apple")
        verify(sqs, times(1)).deleteMessageBatchAsync(anyString(), anyList())
    }
}
