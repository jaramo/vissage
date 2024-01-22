package org.jaramo.vissage.adapter.api

import org.jaramo.vissage.commons.testing.SpringContextTest
import org.jaramo.vissage.infrastructure.UserArgumentResolver.Companion.USER_ID_HEADER
import org.junit.jupiter.api.Nested
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
}
