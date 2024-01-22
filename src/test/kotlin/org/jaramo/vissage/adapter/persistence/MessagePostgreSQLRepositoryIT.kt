package org.jaramo.vissage.adapter.persistence

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.result.shouldBeSuccess
import io.kotest.matchers.shouldBe
import org.jaramo.vissage.commons.testing.IntegrationTestContext
import org.jaramo.vissage.domain.model.Message
import org.jaramo.vissage.fixtures.Users
import org.jaramo.vissage.fixtures.Users.Alice
import org.jaramo.vissage.fixtures.Users.Bob
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import java.time.LocalDateTime
import java.util.UUID

@IntegrationTestContext
class MessagePostgreSQLRepositoryIT @Autowired constructor(
    private val userRepository: UserPostgreSQLRepository,
    private val messageRepository: MessagePostgreSQLRepository,
    private val jdbcTemplate: JdbcTemplate,
) {

    @BeforeEach
    fun setUp() {
        userRepository.save(Alice)
        userRepository.save(Bob)
        userRepository.save(Users.Carol)
    }

    @AfterEach
    fun tearDown() {
        jdbcTemplate.execute("delete from message where true")
        jdbcTemplate.execute("""delete from "user" where true""")
    }

    @Test
    fun `test repository`() {
        val message = Message(
            id = UUID.randomUUID(),
            from = Alice,
            to = Bob,
            content = "Hi Bob!",
            sentAt = LocalDateTime.now()
        )

        messageRepository.getSentBy(Alice.id).shouldBeEmpty()
        messageRepository.getReceivedBy(Bob.id).shouldBeEmpty()
        messageRepository.getReceived(from = Alice.id, to = Bob.id).shouldBeEmpty()

        messageRepository.save(message).shouldBeSuccess { saved ->
            saved shouldBe message
        }

        messageRepository.getSentBy(Alice.id).shouldContainAll(message)
        messageRepository.getReceivedBy(Bob.id).shouldContainAll(message)
        messageRepository.getReceived(from = Alice.id, to = Bob.id).shouldContainAll(message)

        messageRepository.getSentBy(Bob.id).shouldBeEmpty()
        messageRepository.getReceivedBy(Alice.id).shouldBeEmpty()
        messageRepository.getReceived(from = Bob.id, to = Alice.id).shouldBeEmpty()
    }
}
