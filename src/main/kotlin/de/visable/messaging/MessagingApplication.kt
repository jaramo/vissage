package de.visable.messaging

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication
@EnableTransactionManagement
class MessagingApplication

fun main(args: Array<String>) {
	runApplication<MessagingApplication>(*args)
}
