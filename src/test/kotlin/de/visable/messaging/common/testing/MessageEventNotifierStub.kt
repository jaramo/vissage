package de.visable.messaging.common.testing

import de.visable.messaging.domain.model.Message
import de.visable.messaging.domain.service.MessageEventNotifier

class MessageEventNotifierStub : MessageEventNotifier, ClearableStub {

    private val storage = mutableSetOf<Message>()

    override fun messageSent(message: Message): Result<Unit> {
        storage.add(message)
        return Result.success(Unit)
    }

    override fun clear() {
        storage.clear()
    }
}
