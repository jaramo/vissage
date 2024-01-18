package org.jaramo.vissage.adapter.persistence.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.util.UUID

@Table("users")
data class User(
    @Id @Column("user_id") val id: UUID,
    val nickname: String,
    val createdAt: LocalDateTime,
)
