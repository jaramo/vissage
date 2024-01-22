package org.jaramo.vissage.adapter.persistence

import org.jaramo.vissage.commons.testing.ClearableStub
import org.jaramo.vissage.domain.model.Message
import org.jaramo.vissage.domain.service.MessageRepository
import java.util.UUID

class MessageRepositoryStub : MessageRepository, ClearableStub {

    private val storage = mutableSetOf<Message>()

    override fun clear() {
        storage.clear()
    }

    override fun save(message: Message): Result<Message> {
        storage.add(message)
        return Result.success(message)
    }

    override fun getReceivedBy(userId: UUID): List<Message> {
        return storage.filter {
            it.to.id == userId
        }
    }

    override fun getSentBy(userId: UUID): List<Message> {
        return storage.filter {
            it.from.id == userId
        }
    }
}
