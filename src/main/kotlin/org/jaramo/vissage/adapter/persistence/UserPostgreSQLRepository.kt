package org.jaramo.vissage.adapter.persistence

import org.jaramo.vissage.domain.model.User
import org.jaramo.vissage.domain.service.UserRepository
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID
import org.jaramo.vissage.adapter.persistence.entity.User as EntityUser

interface UserSpringRepository : CrudRepository<EntityUser, UUID>, PagingAndSortingRepository<EntityUser, UUID> {
    fun findByNickname(nickname: String): EntityUser?
}

@Repository
class UserPostgreSQLRepository(
    private val repository: UserSpringRepository,
) : UserRepository {

    override fun findUserByNick(nickname: String): User? {
        return repository.findByNickname(nickname)?.toModel()
    }

    override fun save(user: User): Result<User> =
        repository.runCatching {
            this.save(user.toEntity())
        }.map {
            it.toModel()
        }

    private fun User.toEntity(): EntityUser = EntityUser(id, nickname, LocalDateTime.now())

    private fun EntityUser.toModel(): User = User(id, nickname)
}
