package org.jaramo.vissage.adapter.api

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.jaramo.vissage.adapter.api.dto.toDto
import org.jaramo.vissage.application.MessageService
import org.jaramo.vissage.domain.model.User
import org.jaramo.vissage.infrastructure.toResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/message")
class MessageController(
    private val messageService: MessageService,
) {

    @PostMapping
    fun send(user: User, @Valid @RequestBody request: SendMessageRequestDto): ResponseEntity<out Any> {
        TODO()
    }

    @GetMapping("/sent")
    fun sent(user: User): ResponseEntity<out Any> {
        return messageService
            .getSentMessages(user).map { messages ->
                ResponseEntity.ok(
                    messages.map { it.toDto() }
                )
            }.getOrElse {
                it.toResponse()
            }
    }

    @GetMapping("/received")
    fun received(user: User): ResponseEntity<out Any> {
        return messageService
            .getReceivedMessages(user).map { messages ->
                ResponseEntity.ok(
                    messages.map { it.toDto() }
                )
            }.getOrElse {
                it.toResponse()
            }
    }

    @GetMapping("/received", params = ["from"])
    fun receivedFromUser(user: User, @RequestParam("from") from: UUID): ResponseEntity<out Any> {
        return messageService
            .getReceivedMessages(receiver = user, senderId = from).map { messages ->
                ResponseEntity.ok(
                    messages.map { it.toDto() }
                )
            }.getOrElse {
                it.toResponse()
            }
    }
}

data class SendMessageRequestDto(
    val to: UUID,
    @get:NotBlank val message: String,
)
