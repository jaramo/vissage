package org.jaramo.vissage.application

import org.jaramo.vissage.domain.model.ApplicationError.ReceiverNotValidError
import org.jaramo.vissage.domain.model.Message
import org.jaramo.vissage.domain.model.User
import org.jaramo.vissage.domain.model.UserNotFoundException
import org.jaramo.vissage.domain.service.MessageRepository
import org.jaramo.vissage.domain.service.UserRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID
import kotlin.Result.Companion.failure
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@Service
class MessageService(
    private val messageRepository: MessageRepository,
    private val userRepository: UserRepository,
) {

    //    @Transactional
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
        }
    }

    fun getSentMessages(from: User): List<Message> = messageRepository.getSentBy(from.id)
}

@OptIn(ExperimentalContracts::class)
public inline fun <R, T> Result<T>.flatMap(transform: (value: T) -> Result<R>): Result<R> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return this.mapCatching {
        transform(it).getOrThrow()
    }
}
