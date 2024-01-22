package org.jaramo.vissage.adapter.persistence

import org.jaramo.vissage.common.Logging.getLoggerForClass
import org.jaramo.vissage.domain.model.ApplicationError.MessagePersistError
import org.jaramo.vissage.domain.model.Message
import org.jaramo.vissage.domain.service.MessageRepository
import org.jaramo.vissage.domain.service.UserRepository
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.jdbc.core.mapping.AggregateReference
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

interface MessageSpringRepository : CrudRepository<MessageEntity, UUID>,
    PagingAndSortingRepository<MessageEntity, UUID> {
        fun findAllBySenderOrderBySentAtDesc(id: AggregateReference<EntityUser, UUID>): Iterable<MessageEntity>
        fun findAllByReceiverOrderBySentAtDesc(id: AggregateReference<EntityUser, UUID>): Iterable<MessageEntity>
        fun findAllBySenderAndReceiverOrderBySentAtDesc(
            sender: AggregateReference<EntityUser, UUID>,
            receiver: AggregateReference<EntityUser, UUID>,
        ): Iterable<MessageEntity>
}

@Repository
class MessagePostgreSQLRepository(
    private val springRepository: MessageSpringRepository,
    private val userRepository: UserRepository,
) : MessageRepository {

    private val log = getLoggerForClass()

    override fun save(message: Message): Result<Message> =
        springRepository.runCatching {
            this.save(message.toEntity())
        }.onFailure { error ->
            log.error("Error persisting message entity", error)
        }.mapCatching {
            Message(
                id = it.id,
                from = message.from,
                message.to,
                content = message.content,
                sentAt = message.sentAt
            )
        }.recoverCatching { cause ->
            throw MessagePersistError(message, cause)
        }

    override fun getSentBy(userId: UUID): List<Message> {
        return springRepository
                .findAllBySenderOrderBySentAtDesc(AggregateReference.to(userId))
                .map { it.toModel() }
    }

    override fun getReceivedBy(userId: UUID): List<Message> {
        return springRepository
            .findAllByReceiverOrderBySentAtDesc(AggregateReference.to(userId))
            .map { it.toModel() }
    }

    override fun getReceived(from: UUID, to: UUID): List<Message> {
        return springRepository
            .findAllBySenderAndReceiverOrderBySentAtDesc(
                sender = AggregateReference.to(from),
                receiver = AggregateReference.to(to)
            )
            .map { it.toModel() }
    }

    private fun Message.toEntity(): MessageEntity =
        MessageEntity(
            id = id,
            sender = AggregateReference.to(from.id),
            receiver = AggregateReference.to(to.id),
            content = content,
            sentAt = sentAt
        )

    private fun MessageEntity.toModel(): Message =
        Message(
            id = id,
            from = userRepository.findUserById(sender.id!!)!!,
            to = userRepository.findUserById(receiver.id!!)!!,
            content = content,
            sentAt = sentAt
        )
}

@Table("message")
data class MessageEntity(
    @Id private val id: UUID,
    @Column("sender_id") val sender: AggregateReference<EntityUser, UUID>,
    @Column("receiver_id") val receiver: AggregateReference<EntityUser, UUID>,
    val content: String,
    val sentAt: LocalDateTime,
    val deliveredAt: LocalDateTime? = null,
    val readAt: LocalDateTime? = null,

    val createdAt: LocalDateTime? = null,
) : Persistable<UUID> {
    override fun getId(): UUID = id
    override fun isNew(): Boolean = createdAt == null
}
