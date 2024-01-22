package org.jaramo.vissage.application

import org.jaramo.vissage.domain.model.ApplicationError.ExceptionError
import org.jaramo.vissage.domain.model.ApplicationError.ReceiverNotValidError
import org.jaramo.vissage.domain.model.Message
import org.jaramo.vissage.domain.model.User
import org.jaramo.vissage.domain.model.UserNotFoundException
import org.jaramo.vissage.domain.service.MessageEventNotifier
import org.jaramo.vissage.domain.service.MessageRepository
import org.jaramo.vissage.domain.service.UserRepository
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
