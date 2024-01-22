package org.jaramo.vissage.domain.model

import java.util.UUID

sealed class ApplicationError(val error: String, override val cause: Throwable? = null) : Error(error, cause) {
    data class UserAlreadyExistsError(val nickname: String) :
        ApplicationError(error = "User '${nickname}' already exists")

    data class ReceiverNotValidError(val sender: UUID, val receiver: UUID) :
        ApplicationError(error = "Impossible to send message from User['$sender'] to User['$receiver']")

    data class UserPersistError(val user: User, override val cause: Throwable) :
        ApplicationError("Unable to persist user $user", cause)

    data class MessagePersistError(val msg: Message, override val cause: Throwable) :
        ApplicationError("Unable to persist message $msg", cause)

    data class ExceptionError(val whenever: String, override val cause: Throwable) :
        ApplicationError("Error happened when $whenever: ${cause.stackTraceToString()}", cause)
}

class UserNotFoundException(userId: UUID) : RuntimeException("User['$userId'] not found")
class MissingUserIdHeaderException : RuntimeException("Missing 'X-user-id' header")
