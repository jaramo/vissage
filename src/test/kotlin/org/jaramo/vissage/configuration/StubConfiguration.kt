package org.jaramo.vissage.configuration

import org.jaramo.vissage.adapter.persistence.UserRepositoryStub
import org.jaramo.vissage.domain.service.UserRepository
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary


@TestConfiguration
class StubConfiguration {

    @Bean
    @Primary
    fun userRepository(): UserRepository = UserRepositoryStub()

}