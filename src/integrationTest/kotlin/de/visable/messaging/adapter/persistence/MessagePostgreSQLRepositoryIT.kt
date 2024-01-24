package de.visable.messaging.adapter.persistence

import de.visable.messaging.configuration.IntegrationTestContext
import de.visable.messaging.domain.model.Message
import de.visable.messaging.fixtures.Users.Alice
import de.visable.messaging.fixtures.Users.Bob
import de.visable.messaging.fixtures.Users.Carol
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.result.shouldBeSuccess
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
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
        userRepository.save(Carol)
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

        messageRepository.getSentBy(Alice.id).should {
            it shouldHaveSize 1
            it shouldContain message
        }
        messageRepository.getReceivedBy(Bob.id).shouldContainAll(message)
        messageRepository.getReceived(from = Alice.id, to = Bob.id).shouldContainAll(message)

        messageRepository.getSentBy(Bob.id).shouldBeEmpty()
        messageRepository.getReceivedBy(Alice.id).shouldBeEmpty()
        messageRepository.getReceived(from = Bob.id, to = Alice.id).shouldBeEmpty()
    }
}
