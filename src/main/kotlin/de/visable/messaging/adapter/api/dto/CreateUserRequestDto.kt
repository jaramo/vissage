package de.visable.messaging.adapter.api.dto

import jakarta.validation.constraints.NotBlank

data class CreateUserRequestDto(
    @get:NotBlank val nickname: String,
)
