package de.visable.messaging.configuration

import de.visable.messaging.common.testing.MessageEventNotifierStub
import de.visable.messaging.common.testing.MessageRepositoryStub
import de.visable.messaging.common.testing.UserRepositoryStub
import de.visable.messaging.domain.service.MessageEventNotifier
import de.visable.messaging.domain.service.MessageRepository
import de.visable.messaging.domain.service.UserRepository
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary


@TestConfiguration
class StubConfiguration {

    @Bean
    @Primary
    fun userRepository(): UserRepository = UserRepositoryStub()

    @Bean
    @Primary
    fun messageRepository(): MessageRepository = MessageRepositoryStub()

    @Bean
    @Primary
    fun messageEventNotifier(): MessageEventNotifier = MessageEventNotifierStub()
}
