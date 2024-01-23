package de.visable.messaging.application

import de.visable.messaging.application.UserArgumentResolver.Companion.USER_ID_HEADER
import de.visable.messaging.domain.model.MissingUserIdHeaderException
import de.visable.messaging.domain.model.Nickname
import de.visable.messaging.domain.model.User
import de.visable.messaging.domain.model.UserNotFoundException
import de.visable.messaging.domain.service.UserRepository
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.reset
import org.mockito.kotlin.stubbing
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.method.support.ModelAndViewContainer
import java.util.UUID

internal class UserArgumentResolverTest {

    private val userRepository: UserRepository = mock {}
    private val argumentResolver = UserArgumentResolver(userRepository)

    @BeforeEach
    fun setUp() {
        reset(userRepository)
    }

    @Test
    fun `should throw exception when user does not exists`() {
        val methodParameter = mock<MethodParameter> {}
        val mavContainer = mock<ModelAndViewContainer> {}
        val binderFactory = mock<WebDataBinderFactory> {}
        val request = mock<ServletWebRequest> {
            on { getHeader(USER_ID_HEADER) } doReturn null
        }

        shouldThrow<MissingUserIdHeaderException> {
            argumentResolver.resolveArgument(methodParameter, mavContainer, request, binderFactory)
        }
    }

    @Test
    fun `should throw exception when header is not present`() {
        val methodParameter = mock<MethodParameter> {}
        val mavContainer = mock<ModelAndViewContainer> {}
        val binderFactory = mock<WebDataBinderFactory> {}

        val user = User(UUID.randomUUID(), Nickname("test"))
        val request = mock<ServletWebRequest> {
            on { getHeader(USER_ID_HEADER) } doReturn user.id.toString()
        }
        stubbing(userRepository) {
            on { findUserById(user.id) } doReturn null
        }

        shouldThrow<UserNotFoundException> {
            argumentResolver.resolveArgument(methodParameter, mavContainer, request, binderFactory)
        } should {
            it.message shouldBe "User['${user.id}'] not found"
        }
    }

    @Test
    fun `should return user`() {
        val methodParameter = mock<MethodParameter> {}
        val mavContainer = mock<ModelAndViewContainer> {}
        val binderFactory = mock<WebDataBinderFactory> {}

        val user = User(UUID.randomUUID(), Nickname("test"))
        val request = mock<ServletWebRequest> {
            on { getHeader(USER_ID_HEADER) } doReturn user.id.toString()
        }
        stubbing(userRepository) {
            on { findUserById(user.id) } doReturn user
        }

        shouldNotThrowAny {
            argumentResolver.resolveArgument(methodParameter, mavContainer, request, binderFactory)
        }.shouldBeInstanceOf<User>().shouldBe(user)
    }
}
