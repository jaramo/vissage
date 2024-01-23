package de.visable.messaging.domain.service

import de.visable.messaging.domain.model.Nickname
import de.visable.messaging.domain.model.User
import java.util.UUID

interface UserRepository {
    fun save(user: User): Result<User>
    fun findUserById(id: UUID): User?
    fun findUserByNick(nickname: String): User?
}
