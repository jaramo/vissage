package de.visable.messaging.adapter.event

import com.fasterxml.jackson.databind.ObjectMapper
import de.visable.messaging.common.Logging.getLoggerForClass
import de.visable.messaging.domain.model.Message
import de.visable.messaging.domain.service.MessageEventNotifier
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
