package org.jaramo.vissage.commons.testing

import org.jaramo.vissage.configuration.StubConfiguration
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import org.springframework.beans.factory.getBeansOfType
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

@ActiveProfiles("test")
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [
        StubConfiguration::class,
    ],
)
@AutoConfigureMockMvc
@Target(AnnotationTarget.CLASS)
@ExtendWith(SpringContextTestCallbacks::class)
annotation class SpringContextTest

internal class SpringContextTestCallbacks : BeforeEachCallback {
    override fun beforeEach(context: ExtensionContext) {
        val springContext = SpringExtension.getApplicationContext(context)

        springContext.getBeansOfType<ClearableStub>()
            .values
            .forEach { it.clear() }
    }
}