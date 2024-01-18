package org.jaramo.vissage.adapter.api

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.jaramo.vissage.application.UserService
import org.jaramo.vissage.domain.model.ApplicationError
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
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
    fun register(@Valid @RequestBody requestDto: CreateUserRequestDto): ResponseEntity<out Any> {
        val z =
            userService.register(requestDto.nickname).map {
                ResponseEntity.created(
                    ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(it.id).toUri()
                ).body(UserDto(it.id, it.nickname))
            }.getOrElse {
                when (it) {
                    is ApplicationError -> it.toResponse()
                    else -> ResponseEntity.internalServerError().body(it)
                }
            }

        return z
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(error: MethodArgumentNotValidException): ResponseEntity<out Any> {
        val m = error.bindingResult.allErrors.associate {
            (it as FieldError).field to it.defaultMessage.orEmpty()
        }

        return ResponseEntity.badRequest().body(m)
    }

}

data class CreateUserRequestDto(
    @get:NotBlank val nickname: String,
)

data class UserDto(val id: UUID, val nickname: String)
data class ErrorDto(val error: String)

fun ApplicationError.toResponse(): ResponseEntity<ErrorDto> =
    when (this) {
        is ApplicationError.UserAlreadyExistsError -> HttpStatus.BAD_REQUEST
    }.let { status ->
        ResponseEntity.status(status).body(ErrorDto(this.error))
    }
