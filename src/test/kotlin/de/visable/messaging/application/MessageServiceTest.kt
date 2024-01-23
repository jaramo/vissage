package de.visable.messaging.application

import io.kotest.matchers.result.shouldBeFailure
import io.kotest.matchers.result.shouldBeSuccess
import io.kotest.matchers.shouldBe
import de.visable.messaging.domain.model.ApplicationError.ReceiverNotValidError
import de.visable.messaging.domain.model.Message
import de.visable.messaging.domain.model.Nickname
import de.visable.messaging.domain.model.User
import de.visable.messaging.domain.model.UserNotFoundException
import de.visable.messaging.domain.service.MessageEventNotifier
import de.visable.messaging.domain.service.MessageRepository
import de.visable.messaging.domain.service.MessageService
import de.visable.messaging.domain.service.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.reset
import org.mockito.kotlin.stubbing
import org.mockito.kotlin.verify
import java.util.UUID

internal class MessageServiceTest {
    @Nested
    inner class SendMessage {

        private val messageRepository: MessageRepository = mock()
        private val userRepository: UserRepository = mock()
        private val notifier: MessageEventNotifier = mock()
        private val service = MessageService(messageRepository, userRepository, notifier)

        @BeforeEach
        fun setUp() {
            reset(messageRepository, userRepository)
        }

        @Test
        fun `should return error when sending message to own user id`() {
            val sender = User(UUID.randomUUID(), Nickname("Alice"))
            service.sendMessage(from = sender, to = sender.id, "Hi Bob!")
                .shouldBeFailure<ReceiverNotValidError>()

            verify(notifier) {
                0 * { messageSent(any()) }
            }
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

            verify(notifier) {
                0 * { messageSent(any()) }
            }
        }

        @Test
        fun `should persist message, publish event and return success`() {
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
                verify(notifier) {
                    1 * { messageSent(message) }
                }
            }

        }
    }
}
