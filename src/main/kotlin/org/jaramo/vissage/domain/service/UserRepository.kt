package org.jaramo.vissage.domain.service

import org.jaramo.vissage.domain.model.User

interface UserRepository {
    fun findUserByNick(nickname: String): User?
    fun save(user: User): Result<User>
}