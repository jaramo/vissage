package org.jaramo.vissage.adapter.api

import org.hamcrest.Matchers.hasSize
import org.jaramo.vissage.commons.testing.SpringContextTest
import org.jaramo.vissage.domain.service.MessageRepository
import org.jaramo.vissage.domain.service.UserRepository
import org.jaramo.vissage.fixtures.Users.Alice
import org.jaramo.vissage.fixtures.Users.Bob
import org.jaramo.vissage.fixtures.Users.Carol
import org.jaramo.vissage.fixtures.message
import org.jaramo.vissage.infrastructure.UserArgumentResolver.Companion.USER_ID_HEADER
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.util.UUID

@SpringContextTest
class MessageControllerTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val userRepository: UserRepository,
    private val messageRepository: MessageRepository,
) {

    @Nested
    inner class Authentication {

        @ParameterizedTest
        @ValueSource(strings = ["sent", "received"])
        fun `should return 400 when header is missing`(path: String) {
            mockMvc.get("/message/$path") {
                contentType = MediaType.APPLICATION_JSON
            }.andExpect {
                status { isBadRequest() }
                content { contentType(MediaType.APPLICATION_JSON) }
                content {
                    jsonPath("$.error") {
                        isNotEmpty()
                    }
                }
            }
        }

        @ParameterizedTest
        @ValueSource(strings = ["sent", "received"])
        fun `should return 401 when user id doesn't exist`(path: String) {
            mockMvc.get("/message/$path") {
                contentType = MediaType.APPLICATION_JSON
                headers {
                    header(USER_ID_HEADER, UUID.randomUUID())
                }
            }.andExpect {
                status { isUnauthorized() }
                content { contentType(MediaType.APPLICATION_JSON) }
                content {
                    jsonPath("$.error") {
                        isNotEmpty()
                    }
                }
            }
        }
    }

    @Nested
    inner class MessagesReceivedByUser {

        @BeforeEach
        fun setUp() {
            userRepository.save(Alice)
            userRepository.save(Bob)
            userRepository.save(Carol)
        }

        @Test
        fun `should return 200 with empty list`() {
            mockMvc.get("/message/received") {
                headers {
                    header(USER_ID_HEADER, Alice.id)
                }
            }.andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                content {
                    jsonPath("$") {
                        isArray()
                        isEmpty()
                    }
                }
            }
        }

        @Test
        fun `should return a list with 2 messages`() {
            messageRepository.save(message(from = Alice, to = Bob, "Hi Bob!"))
            messageRepository.save(message(from = Bob, to = Alice, "Hi Alice!"))

            messageRepository.save(message(from = Alice, to = Carol, "Hi Carol!"))
            messageRepository.save(message(from = Carol, to = Alice, "Hi Alice!"))

            mockMvc.get("/message/received") {
                headers {
                    header(USER_ID_HEADER, Alice.id)
                }
            }.andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                content {
                    jsonPath("$") {
                        isArray()
                        value(hasSize<Any>(2))
                    }
                    jsonPath("$[0].from.nickname") { value(Bob.nickname.value()) }
                    jsonPath("$[0].to.nickname") { value(Alice.nickname.value()) }
                    jsonPath("$[1].from.nickname") { value(Carol.nickname.value()) }
                    jsonPath("$[1].to.nickname") { value(Alice.nickname.value()) }
                }
            }
        }
    }

    @Nested
    inner class MessagesSentByUser {

        @BeforeEach
        fun setUp() {
            userRepository.save(Alice)
            userRepository.save(Bob)
            userRepository.save(Carol)
        }

        @Test
        fun `should return 200 with empty list`() {
            mockMvc.get("/message/sent") {
                headers {
                    header(USER_ID_HEADER, Alice.id)
                }
            }.andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                content {
                    jsonPath("$") {
                        isArray()
                        isEmpty()
                    }
                }
            }
        }

        @Test
        fun `should return a list with 2 messages`() {
            messageRepository.save(message(from = Alice, to = Bob, "Hi Bob!"))
            messageRepository.save(message(from = Bob, to = Alice, "Hi Alice!"))

            messageRepository.save(message(from = Alice, to = Carol, "Hi Carol!"))
            messageRepository.save(message(from = Carol, to = Alice, "Hi Alice!"))

            mockMvc.get("/message/sent") {
                headers {
                    header(USER_ID_HEADER, Alice.id)
                }
            }.andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                content {
                    jsonPath("$") {
                        isArray()
                        value(hasSize<Any>(2))
                    }
                    jsonPath("$[0].from.nickname") { value(Alice.nickname.value()) }
                    jsonPath("$[0].to.nickname") { value(Bob.nickname.value()) }
                    jsonPath("$[1].from.nickname") { value(Alice.nickname.value()) }
                    jsonPath("$[1].to.nickname") { value(Carol.nickname.value()) }
                }
            }
        }
    }

}
