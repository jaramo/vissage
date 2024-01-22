package org.jaramo.vissage.infrastructure

import org.jaramo.vissage.domain.model.User
import org.jaramo.vissage.domain.model.UserNotFoundException
import org.jaramo.vissage.domain.model.MissingUserIdHeaderException
import org.jaramo.vissage.domain.service.UserRepository
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import java.util.UUID


class UserArgumentResolver(
    private val userRepository: UserRepository,
) : HandlerMethodArgumentResolver {

    companion object {
        const val USER_ID_HEADER = "X-UserId"
    }

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameter.type == User::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): Any {
        val request = webRequest as ServletWebRequest
        val value = request.getHeader(USER_ID_HEADER) ?: throw MissingUserIdHeaderException()
        val userId = UUID.fromString(value)

        return userRepository.findUserById(userId) ?: throw UserNotFoundException(userId)
    }
}


