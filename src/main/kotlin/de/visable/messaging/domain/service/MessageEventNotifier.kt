package de.visable.messaging.domain.service

import de.visable.messaging.domain.model.Message

interface MessageEventNotifier {
    fun messageSent(message: Message): Result<Unit>
}
