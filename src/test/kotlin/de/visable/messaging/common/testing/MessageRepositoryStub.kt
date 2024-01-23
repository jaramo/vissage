package de.visable.messaging.common.testing

import de.visable.messaging.domain.model.Message
import de.visable.messaging.domain.service.MessageRepository
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

    override fun getReceived(from: UUID, to: UUID): List<Message> {
        return storage.filter {
            it.to.id == to && it.from.id == from
        }
    }

    override fun getSentBy(userId: UUID): List<Message> {
        return storage.filter {
            it.from.id == userId
        }
    }
}
