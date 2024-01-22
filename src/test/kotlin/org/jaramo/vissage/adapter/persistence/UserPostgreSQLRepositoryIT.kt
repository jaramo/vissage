package org.jaramo.vissage.adapter.persistence

import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.result.shouldBeSuccess
import io.kotest.matchers.shouldBe
import org.jaramo.vissage.commons.testing.IntegrationTestContext
import org.jaramo.vissage.fixtures.Users.Alice
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate

@IntegrationTestContext
class UserPostgreSQLRepositoryIT @Autowired constructor(
    private val userRepository: UserPostgreSQLRepository,
    private val jdbcTemplate: JdbcTemplate,
) {

    @AfterEach
    fun tearDown() {
        jdbcTemplate.execute("""delete from "user" where true""")
    }

    @Test
    fun `test repository`() {
        userRepository.findUserById(Alice.id).shouldBeNull()
        userRepository.findUserByNick(Alice.nickname.value()).shouldBeNull()
        userRepository.save(Alice).shouldBeSuccess { saved ->
            saved shouldBe Alice
        }
    }
}
