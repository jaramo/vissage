package de.visable.messaging.domain.service

import de.visable.messaging.domain.model.Message
import java.util.UUID

interface MessageRepository {
    fun save(message: Message): Result<Message>
    fun getSentBy(userId: UUID): List<Message>
    fun getReceivedBy(userId: UUID): List<Message>
    fun getReceived(from: UUID, to: UUID): List<Message>
}
