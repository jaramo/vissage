package de.visable.messaging.adapter.api.dto

import de.visable.messaging.domain.model.Nickname
import de.visable.messaging.domain.model.User
import java.util.UUID

data class UserDto(val id: UUID, val nickname: Nickname)

fun User.toDto(): UserDto = UserDto(id, nickname)
