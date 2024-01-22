package org.jaramo.vissage.adapter.persistence

import org.jaramo.vissage.common.Logging.getLoggerForClass
import org.jaramo.vissage.domain.model.ApplicationError.UserPersistError
import org.jaramo.vissage.domain.model.Nickname
import org.jaramo.vissage.domain.model.User
import org.jaramo.vissage.domain.service.UserRepository
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

interface UserSpringRepository : CrudRepository<EntityUser, UUID>, PagingAndSortingRepository<EntityUser, UUID> {
    fun findByNickname(nickname: String): EntityUser?
}

@Repository
class UserPostgreSQLRepository(
    private val repository: UserSpringRepository,
) : UserRepository {

    private val log = getLoggerForClass()

    override fun save(user: User): Result<User> =
        repository.runCatching {
            this.save(
                EntityUser(
                    id = user.id,
                    nickname = user.nickname.value(),
                )
            )
        }.onFailure { error ->
            log.error("Error persisting user entity", error)
        }.mapCatching { entity ->
            entity.toModel()
        }.recoverCatching { cause ->
            throw UserPersistError(user, cause)
        }

    override fun findUserById(id: UUID): User? {
        return repository.findById(id).getOrNull()?.toModel()
    }

    override fun findUserByNick(nickname: String): User? {
        return repository.findByNickname(nickname)?.toModel()
    }

    private fun EntityUser.toModel(): User = User(id, Nickname(nickname))
}

@Table("user")
data class EntityUser(
    @Id private val id: UUID,
    val nickname: String,
    val createdAt: LocalDateTime? = null,
) : Persistable<UUID> {
    override fun getId(): UUID = id
    override fun isNew(): Boolean = createdAt == null
}
