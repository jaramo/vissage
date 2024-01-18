package org.jaramo.vissage.application

import org.jaramo.vissage.domain.model.ApplicationError.UserAlreadyExistsError
import org.jaramo.vissage.domain.model.User
import org.jaramo.vissage.domain.service.UserRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserService(
    private val userRepository: UserRepository,
) {
    fun register(nickname: String): Result<User> {
        return when (userRepository.findUserByNick(nickname)) {
            null -> userRepository.save(User(id = UUID.randomUUID(), nickname = nickname))
            else -> Result.failure(UserAlreadyExistsError(nickname))
        }
    }
}

