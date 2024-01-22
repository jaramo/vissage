package org.jaramo.vissage.application

import io.kotest.matchers.result.shouldBeFailure
import io.kotest.matchers.result.shouldBeSuccess
import io.kotest.matchers.shouldBe
import org.jaramo.vissage.domain.model.ApplicationError.ReceiverNotValidError
import org.jaramo.vissage.domain.model.Message
import org.jaramo.vissage.domain.model.Nickname
import org.jaramo.vissage.domain.model.User
import org.jaramo.vissage.domain.model.UserNotFoundException
import org.jaramo.vissage.domain.service.MessageRepository
import org.jaramo.vissage.domain.service.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.reset
import org.mockito.kotlin.stubbing
import java.util.UUID

internal class MessageServiceTest {
    @Nested
    inner class SendMessage {

        private val messageRepository: MessageRepository = mock()
        private val userRepository: UserRepository = mock()
        private val service = MessageService(messageRepository, userRepository)

        @BeforeEach
        fun setUp() {
            reset(messageRepository, userRepository)
        }

        @Test
        fun `should return error when sending message to own user id`() {
            val sender = User(UUID.randomUUID(), Nickname("Alice"))
            service.sendMessage(from = sender, to = sender.id, "Hi Bob!")
                .shouldBeFailure<ReceiverNotValidError>()
        }

        @Test
        fun `should return error when receiver user does not exists`() {
            val sender = User(UUID.randomUUID(), Nickname("Alice"))
            val receiver = User(UUID.randomUUID(), Nickname("Bob"))

            stubbing(userRepository) {
                on { findUserById(receiver.id) } doReturn null
            }

            service.sendMessage(from = sender, to = receiver.id, "Hi Bob!")
                .shouldBeFailure<UserNotFoundException>()
        }

        @Test
        fun `should persist message and return success`() {
            val sender = User(UUID.randomUUID(), Nickname("Alice"))
            val receiver = User(UUID.randomUUID(), Nickname("Bob"))

            stubbing(userRepository) {
                on { findUserById(receiver.id) } doReturn receiver
            }

            stubbing(messageRepository) {
                on { save(any()) }.then {
                    it.getArgument(0, Message::class.java)
                }
            }

            service.sendMessage(from = sender, to = receiver.id, "Hi Bob!").shouldBeSuccess { message ->
                message.from shouldBe sender
                message.to shouldBe receiver
            }
        }
    }
}
