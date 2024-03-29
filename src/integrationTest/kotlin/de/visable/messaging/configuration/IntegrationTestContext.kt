package de.visable.messaging.configuration

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("integration-test")
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [
        TestContainersConfiguration::class,
    ],
)
@AutoConfigureMockMvc
@Target(AnnotationTarget.CLASS)
annotation class IntegrationTestContext
