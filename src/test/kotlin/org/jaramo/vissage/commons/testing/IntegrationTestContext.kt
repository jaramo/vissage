package org.jaramo.vissage.commons.testing

import org.jaramo.vissage.configuration.TestContainersConfiguration
import org.junit.jupiter.api.extension.ExtendWith
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
@ExtendWith(SpringContextTestCallbacks::class)
annotation class IntegrationTestContext
