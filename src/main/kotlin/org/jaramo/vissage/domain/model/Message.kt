package org.jaramo.vissage.domain.model

import java.time.LocalDateTime
import java.util.UUID

data class Message(
    val id: UUID,
    val from: User,
    val to: User,
    val content: String,
    val sentAt: LocalDateTime,
    val deliveredAt: LocalDateTime? = null,
    val readAt: LocalDateTime? = null,
)
