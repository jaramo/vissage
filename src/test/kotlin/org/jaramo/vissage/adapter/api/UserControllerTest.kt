package org.jaramo.vissage.adapter.api

import org.jaramo.vissage.commons.testing.SpringContextTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@SpringContextTest
internal class UserControllerTest @Autowired constructor(
    private val mockMvc: MockMvc,
) {

    @Test
    fun `should success to register unused nickname`() {
        mockMvc.post("/user") {
            contentType = MediaType.APPLICATION_JSON
            content = """{ "nickname": "test1" }"""
        }.andExpect {
            status { isCreated() }
            content { contentType(MediaType.APPLICATION_JSON) }
            content {
                jsonPath("$.id") {
                    isNotEmpty()
                }
            }
        }
    }

    @Test
    fun `should fail to register same nickname twice`() {
        mockMvc.post("/user") {
            contentType = MediaType.APPLICATION_JSON
            content = """{ "nickname": "test1" }"""
        }.andExpect {
            status { isCreated() }
            content { contentType(MediaType.APPLICATION_JSON) }
            content {
                jsonPath("$.id") {
                    isNotEmpty()
                }
            }
        }

        mockMvc.post("/user") {
            contentType = MediaType.APPLICATION_JSON
            content = """{ "nickname": "test1" }"""
        }.andExpect {
            status { isBadRequest() }
            content { contentType(MediaType.APPLICATION_JSON) }
            content {
                jsonPath("$.error") {
                    value("User 'test1' already exists")
                }
            }
        }
    }

    @Test
    fun `should fail to register empty nickname`() {
        mockMvc.post("/user") {
            contentType = MediaType.APPLICATION_JSON
            content = """{ "nickname": "" }"""
        }.andExpect {
            status { isBadRequest() }
            content { contentType(MediaType.APPLICATION_JSON) }
            content {
                jsonPath("$.nickname") {
                    value("must not be blank")
                }
            }
        }
    }
}