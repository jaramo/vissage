package org.jaramo.vissage.application

import org.jaramo.vissage.domain.model.ApplicationError
import org.jaramo.vissage.domain.model.ApplicationError.ReceiverNotValidError
import org.jaramo.vissage.domain.model.ApplicationError.UserAlreadyExistsError
import org.jaramo.vissage.domain.model.MissingUserIdHeaderException
import org.jaramo.vissage.domain.model.UserNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler


@ControllerAdvice
class ErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(error: MethodArgumentNotValidException): ResponseEntity<out Any> {
        val m = error.bindingResult.allErrors.associate {
            (it as FieldError).field to it.defaultMessage.orEmpty()
        }

        return ResponseEntity.badRequest().body(m)
    }

    @ExceptionHandler(MissingUserIdHeaderException::class)
    fun handleMissingHeaderException(error: MissingUserIdHeaderException): ResponseEntity<out Any> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorDto(error.message!!))
    }

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFoundException(error: UserNotFoundException): ResponseEntity<out Any> {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorDto(error.message!!))
    }
}

fun Throwable.toResponse(): ResponseEntity<ErrorDto> =
    when (this) {
        is ApplicationError -> this.toResponse()
        else -> throw this
    }

fun ApplicationError.toResponse(): ResponseEntity<ErrorDto> =
    when (this) {
        is UserAlreadyExistsError, is ReceiverNotValidError -> HttpStatus.BAD_REQUEST
        else -> throw this
    }.let { status ->
        ResponseEntity.status(status).body(ErrorDto(this.error))
    }

data class ErrorDto(val error: String)
