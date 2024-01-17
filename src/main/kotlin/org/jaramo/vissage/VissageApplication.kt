package org.jaramo.vissage

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class VissageApplication

fun main(args: Array<String>) {
	runApplication<VissageApplication>(*args)
}
