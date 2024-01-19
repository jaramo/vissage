package org.jaramo.vissage.domain.model

import java.util.UUID

sealed class ApplicationError(val error: String) : Error(error) {
    data class UserAlreadyExistsError(val nickname: String) :
        ApplicationError(error = "User '${nickname}' already exists")

    data class ReceiverNotValidError(val sender: UUID, val receiver: UUID) :
        ApplicationError(error = "Impossible to send message from User['$sender'] to User['$receiver']")
}

class UserNotFoundException(userId: UUID) : RuntimeException("User['$userId'] not found")
