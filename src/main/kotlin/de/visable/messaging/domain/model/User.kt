package de.visable.messaging.domain.model

import java.util.UUID

data class User(val id: UUID, val nickname: Nickname)

@JvmInline
value class Nickname(private val s: String) {
    init {
        require(s.isNotEmpty()) {
            "Nickname can't be empty"
        }
    }

    fun value() = s
}
