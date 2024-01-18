package org.jaramo.vissage.adapter.persistence

import org.jaramo.vissage.commons.testing.ClearableStub
import org.jaramo.vissage.domain.model.User
import org.jaramo.vissage.domain.service.UserRepository

class UserRepositoryStub : UserRepository, ClearableStub {

    private val users = mutableSetOf<User>()
    override fun findUserByNick(nickname: String): User? =
        users.find {
            it.nickname == nickname
        }

    override fun save(user: User): Result<User> {
        users.add(user)
        return Result.success(user)
    }

    override fun clear() {
        users.clear()
    }
}