package de.visable.messaging.domain.service

import de.visable.messaging.common.Logging.getLoggerForClass
import de.visable.messaging.domain.model.ApplicationError.ExceptionError
import de.visable.messaging.domain.model.ApplicationError.ReceiverNotValidError
import de.visable.messaging.domain.model.Message
import de.visable.messaging.domain.model.User
import de.visable.messaging.domain.model.UserNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID
import kotlin.Result.Companion.failure

@Service
class MessageService(
    private val messageRepository: MessageRepository,
    private val userRepository: UserRepository,
    private val notifier: MessageEventNotifier,
) {

    private val log = getLoggerForClass()

    @Transactional
    fun sendMessage(from: User, to: UUID, content: String): Result<Message> {
        if (from.id == to)
            return failure(ReceiverNotValidError(sender = from.id, receiver = to))

        return when (val receiver = userRepository.findUserById(to)) {
            null -> failure(UserNotFoundException(to))
            else -> {
                val message = Message(
                    id = UUID.randomUUID(),
                    from = from,
                    to = receiver,
                    content = content,
                    sentAt = LocalDateTime.now()
                )

                messageRepository.save(message)
            }
        }.onSuccess { message ->
            notifier.messageSent(message)
        }.onFailure { error ->
            log.error("Error sending message", error)
        }
    }

    fun getReceivedMessages(receiver: User): Result<List<Message>> =
        runCatching {
            messageRepository.getReceivedBy(receiver.id)
        }.recoverCatching {
            throw ExceptionError(
                whenever = "fetching messages received by User[${receiver.id}]",
                cause = it
            )
        }

    fun getSentMessages(sender: User): Result<List<Message>> =
        runCatching {
            messageRepository.getSentBy(sender.id)
        }.recoverCatching {
            throw ExceptionError(
                whenever = "fetching messages sent by User[${sender.id}]",
                cause = it
            )
        }

    fun getReceivedMessages(receiver: User, senderId: UUID): Result<List<Message>> =
        runCatching {
            messageRepository.getReceived(from = senderId, to = receiver.id)
        }.recoverCatching {
            throw ExceptionError(
                whenever = "fetching messages sent by User[${senderId}] to User[${receiver.id}]",
                cause = it
            )
        }
}
