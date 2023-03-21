package com.robinko.blogsearch

import com.amazonaws.services.sns.AmazonSNS
import com.amazonaws.services.sns.model.PublishRequest
import com.amazonaws.services.sns.model.PublishResult
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.mock
import org.mockito.BDDMockito.times
import org.mockito.BDDMockito.verify
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class SnsServiceTest {

    private lateinit var snsService: SnsService

    @Mock
    private lateinit var sns: AmazonSNS

    @Captor
    private lateinit var publishRequestCaptor: ArgumentCaptor<PublishRequest>

    private val snsTopicPrefix = "arn:aws:sns:ap-northeast-2:000000000000:"

    private val om: ObjectMapper = jacksonObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        .registerModules(JavaTimeModule(), Jdk8Module())

    @BeforeEach
    fun init() {
        snsService = SnsService(snsTopicPrefix, sns)
    }

    @Test
    fun destroy() {
        snsService.destroy()
    }

    @Test
    fun `publish`() {
        // arrange
        val event = SnsEvent("eventName", 0, "entityName", "")
        val messagesStub = mock(PublishResult::class.java)
        given(sns.publish(nonNullAny(PublishRequest::class.java))).willReturn(messagesStub)

        // action
        assertDoesNotThrow { snsService.publish(event) }

        // verify
        verify(sns, times(1)).publish(capture(publishRequestCaptor))
        Assertions.assertThat(publishRequestCaptor.value.message).isEqualTo(om.writeValueAsString(event))
        Assertions.assertThat(publishRequestCaptor.value.topicArn).isEqualTo(snsTopicPrefix + event.entityName)

    }

}
