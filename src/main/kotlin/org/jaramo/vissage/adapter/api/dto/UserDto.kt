package org.jaramo.vissage.adapter.api.dto

import org.jaramo.vissage.domain.model.Nickname
import org.jaramo.vissage.domain.model.User
import java.util.UUID

data class UserDto(val id: UUID, val nickname: Nickname)

fun User.toDto(): UserDto = UserDto(id, nickname)
