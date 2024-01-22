package org.jaramo.vissage.domain.service

import org.jaramo.vissage.domain.model.Message

interface MessageEventNotifier {
    fun messageSent(message: Message): Result<Unit>
}
