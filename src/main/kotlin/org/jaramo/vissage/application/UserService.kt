package org.jaramo.vissage.application

import org.jaramo.vissage.domain.model.ApplicationError.UserAlreadyExistsError
import org.jaramo.vissage.domain.model.Nickname
import org.jaramo.vissage.domain.model.User
import org.jaramo.vissage.domain.service.UserRepository
import org.jaramo.vissage.infrastructure.Logging.getLoggerForClass
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserService(
    private val userRepository: UserRepository,
) {

    private val log = getLoggerForClass()

    fun register(nickname: String): Result<User> {
        return when (userRepository.findUserByNick(nickname)) {
            null -> userRepository.save(User(UUID.randomUUID(), Nickname(nickname)))
            else -> Result.failure(UserAlreadyExistsError(nickname))
        }.onFailure { error ->
            log.error("Error registering user with nickname `$nickname`", error)
        }
    }
}

