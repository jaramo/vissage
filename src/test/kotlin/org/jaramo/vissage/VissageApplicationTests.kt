package org.jaramo.vissage

import org.jaramo.vissage.configuration.TestContainersConfiguration
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(
	classes = [
		TestContainersConfiguration::class
	]
)
class VissageApplicationTests {

	@Test
	fun contextLoads() {
	}

}
