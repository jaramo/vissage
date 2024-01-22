package org.jaramo.vissage.configuration

import org.jaramo.vissage.domain.service.UserRepository
import org.jaramo.vissage.infrastructure.UserArgumentResolver
import org.springframework.stereotype.Component
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Component
class UserArgumentResolverConfiguration(
    private val userRepository: UserRepository
) : WebMvcConfigurer {

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(UserArgumentResolver(userRepository))
    }
}
