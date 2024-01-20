package org.jaramo.vissage.domain.service

import org.jaramo.vissage.domain.model.Message
import java.util.UUID

interface MessageRepository {
    fun save(message: Message): Result<Message>
    fun getSentBy(userId: UUID): List<Message>
}
