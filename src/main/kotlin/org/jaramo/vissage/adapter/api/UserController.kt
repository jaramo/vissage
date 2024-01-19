package org.jaramo.vissage.adapter.api

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.jaramo.vissage.application.UserService
import org.jaramo.vissage.domain.model.Nickname
import org.jaramo.vissage.infrastructure.toResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.util.UUID


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
                ResponseEntity.created(
                    ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(user.id).toUri()
                ).body(UserDto(user.id, user.nickname))
            }.getOrElse { error ->
                error.toResponse()
            }
}

data class CreateUserRequestDto(
    @get:NotBlank val nickname: String,
)

data class UserDto(val id: UUID, val nickname: Nickname)
