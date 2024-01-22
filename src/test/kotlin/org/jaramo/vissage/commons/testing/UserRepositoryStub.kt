package org.jaramo.vissage.commons.testing

import org.jaramo.vissage.domain.model.User
import org.jaramo.vissage.domain.service.UserRepository
import java.util.UUID

class UserRepositoryStub : UserRepository, ClearableStub {

    private val users = mutableSetOf<User>()
    override fun save(user: User): Result<User> {
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
