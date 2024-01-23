package de.visable.messaging.adapter.api

import org.hamcrest.Matchers.emptyArray
import org.hamcrest.Matchers.emptyString
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.not
import de.visable.messaging.application.UserArgumentResolver.Companion.USER_ID_HEADER
import de.visable.messaging.common.testing.SpringContextTest
import de.visable.messaging.domain.service.MessageRepository
import de.visable.messaging.domain.service.UserRepository
import de.visable.messaging.fixtures.Users.Alice
import de.visable.messaging.fixtures.Users.Bob
import de.visable.messaging.fixtures.Users.Carol
import de.visable.messaging.fixtures.message
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.util.UUID

@SpringContextTest
class MessageControllerTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val userRepository: UserRepository,
    private val messageRepository: MessageRepository,
) {

    @BeforeEach
    fun setUp() {
        userRepository.save(Alice)
        userRepository.save(Bob)
        userRepository.save(Carol)
    }

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
    inner class MessagesReceivedByUserFromParticularUser {


        @Test
        fun `should return 200 with empty list`() {
            messageRepository.save(message(from = Alice, to = Bob, "Hi Bob!"))
            messageRepository.save(message(from = Bob, to = Alice, "Hi Alice!"))

            messageRepository.save(message(from = Alice, to = Carol, "Hi Carol!"))

            mockMvc.get("/message/received") {
                param("from", Carol.id.toString())
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
                param("from", Bob.id.toString())
                headers {
                    header(USER_ID_HEADER, Alice.id)
                }
            }.andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                content {
                    jsonPath("$") {
                        isArray()
                        value(hasSize<Any>(1))
                    }
                    jsonPath("$[0].from.nickname") { value(Bob.nickname.value()) }
                    jsonPath("$[0].to.nickname") { value(Alice.nickname.value()) }
                }
            }
        }
    }

    @Nested
    inner class MessagesSentByUser {



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
                        value<Array<Any>>(emptyArray())
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

    @Nested
    inner class SendMessageToAnotherUser {

        @Test
        fun `should return 400 when sending message to yourself`() {
            mockMvc.post("/message") {
                headers {
                    header(USER_ID_HEADER, Alice.id)
                }
                contentType = MediaType.APPLICATION_JSON
                content = """|
                    |{
                    |   "to": "${Alice.id}",
                    |   "message": "Hi Bob!"
                    |}
                    |""".trimMargin()
            }.andExpect {
                status { isBadRequest() }
                content { contentType(MediaType.APPLICATION_JSON) }
                content {
                    jsonPath("$.error") {
                        value(not(emptyString()))
                    }
                }
            }
        }

        @Test
        fun `should return 201 when sending message to another user`() {
            mockMvc.post("/message") {
                headers {
                    header(USER_ID_HEADER, Alice.id)
                }
                contentType = MediaType.APPLICATION_JSON
                content = """|
                    |{
                    |   "to": "${Bob.id}",
                    |   "message": "Hi Bob!"
                    |}
                    |""".trimMargin()
            }.andExpect {
                status { isCreated() }
                content { contentType(MediaType.APPLICATION_JSON) }
                content {
                    jsonPath("$.id") { value(not(emptyString())) }
                    jsonPath("$.from.nickname") { value(Alice.nickname.value()) }
                    jsonPath("$.to.nickname") { value(Bob.nickname.value()) }
                    jsonPath("$.status") { value("SENT") }
                }
            }
        }
    }
}
