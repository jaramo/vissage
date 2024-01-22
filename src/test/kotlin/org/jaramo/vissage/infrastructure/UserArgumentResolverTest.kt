package org.jaramo.vissage.infrastructure

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.jaramo.vissage.application.UserArgumentResolver
import org.jaramo.vissage.application.UserArgumentResolver.Companion.USER_ID_HEADER
import org.jaramo.vissage.domain.model.MissingUserIdHeaderException
import org.jaramo.vissage.domain.model.Nickname
import org.jaramo.vissage.domain.model.User
import org.jaramo.vissage.domain.model.UserNotFoundException
import org.jaramo.vissage.domain.service.UserRepository
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

        val error = shouldThrow<UserNotFoundException> {
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
