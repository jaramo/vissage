package de.visable.messaging.adapter.api

import jakarta.validation.Valid
import de.visable.messaging.adapter.api.dto.CreateUserRequestDto
import de.visable.messaging.adapter.api.dto.toDto
import de.visable.messaging.application.toResponse
import de.visable.messaging.domain.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/user")
class UserController(
    private val userService: UserService,
) {
    @PostMapping
    fun register(@Valid @RequestBody requestDto: CreateUserRequestDto): ResponseEntity<out Any> =
        userService
            .register(requestDto.nickname)
            .map { user ->
                ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(user.toDto())
            }.getOrElse { error ->
                error.toResponse()
            }
}

