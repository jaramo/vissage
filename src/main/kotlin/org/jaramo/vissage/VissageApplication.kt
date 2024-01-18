package org.jaramo.vissage

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication
@EnableTransactionManagement
class VissageApplication

fun main(args: Array<String>) {
	runApplication<VissageApplication>(*args)
}
