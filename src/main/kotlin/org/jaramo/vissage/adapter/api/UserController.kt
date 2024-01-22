package org.jaramo.vissage.adapter.api

import jakarta.validation.Valid
import org.jaramo.vissage.adapter.api.dto.CreateUserRequestDto
import org.jaramo.vissage.adapter.api.dto.toDto
import org.jaramo.vissage.application.toResponse
import org.jaramo.vissage.domain.service.UserService
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

