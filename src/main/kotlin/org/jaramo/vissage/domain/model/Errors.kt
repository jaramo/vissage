package org.jaramo.vissage.domain.model

sealed class ApplicationError(val error: String) : Error(error) {
    data class UserAlreadyExistsError(val nickname: String) :
        ApplicationError(error = "User '${nickname}' already exists")
}