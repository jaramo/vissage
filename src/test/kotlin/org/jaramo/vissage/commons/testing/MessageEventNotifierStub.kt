package org.jaramo.vissage.commons.testing

import org.jaramo.vissage.domain.model.Message
import org.jaramo.vissage.domain.service.MessageEventNotifier

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
