package de.visable.messaging.fixtures

import de.visable.messaging.domain.model.Message
import de.visable.messaging.domain.model.Nickname
import de.visable.messaging.domain.model.User
import java.time.LocalDateTime
import java.util.UUID

object Users {
    val Alice = User(UUID.randomUUID(), Nickname("Alice"))
    val Bob = User(UUID.randomUUID(), Nickname("Bob"))
    val Carol = User(UUID.randomUUID(), Nickname("Carol"))
}


fun message(
    from: User,
    to: User,
    content: String,
    sentAt: LocalDateTime = LocalDateTime.now(),
    deliveredAt: LocalDateTime? = null,
    readAt: LocalDateTime? = null,
): Message =
    Message(
        id = UUID.randomUUID(),
        from = from,
        to = to,
        content = content,
        sentAt = sentAt,
        deliveredAt = deliveredAt,
        readAt = readAt
    )
