package org.jaramo.vissage.adapter.api

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.jaramo.vissage.application.MessageService
import org.jaramo.vissage.domain.model.Nickname
import org.jaramo.vissage.domain.model.User
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
    fun sent(user: User): ResponseEntity<SentMessagesDto> {
        TODO()
    }

    @GetMapping("/received")
    fun received(user: User) {
        TODO()
    }

    @GetMapping("/received", params = ["from"])
    fun receivedFromUser(user: User, @RequestParam("from") from: UUID) {
        TODO()
    }
}

data class SendMessageRequestDto(
    val to: UUID,
    @get:NotBlank val message: String,
)

data class SendMessageResponseDto(
    val id: UUID,
    val status: String,
)

data class SentMessagesDto(val from: Nickname, val messages: List<MessageDto>)
data class MessageDto(val id: UUID, val from: Nickname, val to: Nickname)
