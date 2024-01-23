package de.visable.messaging.adapter.api

import jakarta.validation.Valid
import de.visable.messaging.adapter.api.dto.SendMessageRequestDto
import de.visable.messaging.adapter.api.dto.toDto
import de.visable.messaging.application.toResponse
import de.visable.messaging.domain.model.User
import de.visable.messaging.domain.service.MessageService
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
        return messageService
            .sendMessage(from = user, to = request.to, content = request.message)
            .mapCatching { message ->
                ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(message.toDto())
            }.getOrElse {
                it.toResponse()
            }
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
