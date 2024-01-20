package org.jaramo.vissage.adapter.persistence

import com.fasterxml.jackson.annotation.JsonInclude
import org.jaramo.vissage.domain.model.Message
import org.jaramo.vissage.domain.service.MessageRepository
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

}

@Repository
class MessagePostgreSQLRepository(
    private val repository: MessageSpringRepository,
) : MessageRepository {

    override fun save(message: Message): Result<Message> =
        repository.runCatching {
            this.save(message.toEntity())
        }.map {
            Message(
                id = it.id,
                from = message.from,
                message.to,
                content = message.content,
                sentAt = message.sentAt
            )
        }

    override fun getSentBy(userId: UUID): List<Message> {
        TODO("Not yet implemented")
    }

    private fun Message.toEntity(): MessageEntity =
        MessageEntity(
            sender = AggregateReference.to(from.id),
            receiver = AggregateReference.to(to.id),
            content = content,
            createdAt = LocalDateTime.now(),
            metadata = Metadata(sentAt = sentAt)
        )
}

@Table("message")
data class MessageEntity(
    @Id private val id: UUID? = null,
    @Column("sender_id") val sender: AggregateReference<EntityUser, UUID>,
    @Column("receiver_id") val receiver: AggregateReference<EntityUser, UUID>,
    val content: String,
    val createdAt: LocalDateTime,
    val metadata: Metadata,
) : Persistable<UUID> {
    override fun getId(): UUID? = id
    override fun isNew(): Boolean = id == null
}

@JsonInclude(JsonInclude.Include.ALWAYS)
data class Metadata(
    val sentAt: LocalDateTime,
    val deliveredAt: LocalDateTime? = null,
    val readAt: LocalDateTime? = null,
)
