package de.visable.messaging.adapter.api.dto

import de.visable.messaging.domain.model.Message
import java.util.UUID

data class MessageDto(
    val id: UUID,
    val from: UserDto,
    val to: UserDto,
    val content: String,
    val status: String,
)

fun Message.toDto(): MessageDto =
    MessageDto(
        id = id,
        from = from.toDto(),
        to = to.toDto(),
        content = content,
        status = when {
            readAt != null -> "READ"
            deliveredAt != null -> "DELIVERED"
            else -> "SENT"
        }
    )
