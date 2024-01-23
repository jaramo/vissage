package de.visable.messaging.adapter.api.dto

import jakarta.validation.constraints.NotBlank
import java.util.UUID

data class SendMessageRequestDto(
    val to: UUID,
    @get:NotBlank val message: String,
)
