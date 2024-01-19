package org.jaramo.vissage.adapter.persistence

import org.jaramo.vissage.commons.testing.ClearableStub
import org.jaramo.vissage.domain.model.Nickname
import org.jaramo.vissage.domain.model.User
import org.jaramo.vissage.domain.service.UserRepository
import java.util.UUID

class UserRepositoryStub : UserRepository, ClearableStub {

    private val users = mutableSetOf<User>()
    override fun createUser(nickname: Nickname): Result<User> {
        val user = User(UUID.randomUUID(), nickname)
        users.add(user)
        return Result.success(user)
    }

    override fun findUserById(id: UUID): User? =
        users.find {it.id == id }

    override fun findUserByNick(nickname: String): User? =
        users.find { it.nickname.value() == nickname }

    override fun clear() {
        users.clear()
    }
}
