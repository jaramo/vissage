package org.jaramo.vissage.application

import org.jaramo.vissage.domain.model.ApplicationError.UserAlreadyExistsError
import org.jaramo.vissage.domain.model.Nickname
import org.jaramo.vissage.domain.model.User
import org.jaramo.vissage.domain.service.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
) {

//    @Transactional
    fun register(nickname: String): Result<User> {
        return when (userRepository.findUserByNick(nickname)) {
            null -> userRepository.createUser(Nickname(nickname))
            else -> Result.failure(UserAlreadyExistsError(nickname))
        }
    }
}

