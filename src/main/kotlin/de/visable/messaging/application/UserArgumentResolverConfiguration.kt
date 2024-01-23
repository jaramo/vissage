package de.visable.messaging.application

import de.visable.messaging.domain.service.UserRepository
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
