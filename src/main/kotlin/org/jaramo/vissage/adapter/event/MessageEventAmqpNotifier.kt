package org.jaramo.vissage.adapter.event

import com.fasterxml.jackson.databind.ObjectMapper
import org.jaramo.vissage.adapter.logging.Logging.getLoggerForClass
import org.jaramo.vissage.domain.model.Message
import org.jaramo.vissage.domain.service.MessageEventNotifier
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.core.MessageBuilder
import org.springframework.amqp.core.MessageProperties
import org.springframework.stereotype.Service

@Service
class MessageEventAmqpNotifier(
    private val amqpTemplate: AmqpTemplate,
    private val objectMapper: ObjectMapper,
) : MessageEventNotifier {

    private val log = getLoggerForClass()

    companion object {
        const val EXCHANGE = "events"
    }

    override fun messageSent(message: Message) =
        runCatching {
            val body = objectMapper.writeValueAsBytes(message)
            val amqpMessage = MessageBuilder
                .withBody(body)
                .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                .setContentLength(body.size.toLong())
                .build()

            amqpTemplate.convertAndSend(EXCHANGE, "messaging.message.sent", amqpMessage)
        }.onFailure { error ->
            log.error("Error publishing message $message", error)
        }
}
