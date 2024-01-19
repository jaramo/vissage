package org.jaramo.vissage.domain.service

import org.jaramo.vissage.domain.model.Nickname
import org.jaramo.vissage.domain.model.User
import java.util.UUID

interface UserRepository {
    fun createUser(nickname: Nickname): Result<User>
    fun findUserById(id: UUID): User?
    fun findUserByNick(nickname: String): User?
}
