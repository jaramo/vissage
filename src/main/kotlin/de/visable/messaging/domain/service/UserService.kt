package de.visable.messaging.domain.service

import de.visable.messaging.common.Logging.getLoggerForClass
import de.visable.messaging.domain.model.ApplicationError.UserAlreadyExistsError
import de.visable.messaging.domain.model.Nickname
import de.visable.messaging.domain.model.User
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

