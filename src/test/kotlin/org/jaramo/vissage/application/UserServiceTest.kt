package org.jaramo.vissage.application

import io.kotest.matchers.result.shouldBeFailure
import io.kotest.matchers.result.shouldBeSuccess
import io.kotest.matchers.shouldBe
import org.jaramo.vissage.domain.model.ApplicationError.UserAlreadyExistsError
import org.jaramo.vissage.domain.model.ApplicationError.UserPersistError
import org.jaramo.vissage.domain.model.Nickname
import org.jaramo.vissage.domain.model.User
import org.jaramo.vissage.domain.service.UserRepository
import org.jaramo.vissage.domain.service.UserService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.reset
import org.mockito.kotlin.stubbing
import java.util.UUID

class UserServiceTest {

    private val userRepository: UserRepository = mock()
    private val service = UserService(userRepository)

    @BeforeEach
    fun setUp() {
        reset(userRepository)
    }

    @Test
    fun `should return error when nickname already exists`() {
        val user = User(id = UUID.randomUUID(), Nickname("test"))

        stubbing(userRepository) {
            on { findUserByNick(any()) } doReturn user
        }

        service.register(user.nickname.value())
            .shouldBeFailure<UserAlreadyExistsError>()
    }

    @Test
    fun `should return error when not possible to persist user`() {
        val user = User(id = UUID.randomUUID(), Nickname("test"))

        stubbing(userRepository) {
            on { findUserByNick(user.nickname.value()) } doReturn null
            on { save(any()) } doReturn Result.failure(UserPersistError(user, Error("DB ERROR")))
        }

        service.register(user.nickname.value())
            .shouldBeFailure<UserPersistError>()
    }

    @Test
    fun `should persist user and return success`() {
        val user = User(id = UUID.randomUUID(), Nickname("test"))

        stubbing(userRepository) {
            on { findUserByNick(user.nickname.value()) } doReturn null
            on { save(any()) } doReturn Result.success(user)
        }

        service.register(user.nickname.value())
            .shouldBeSuccess()
            .shouldBe(user)
    }
}
