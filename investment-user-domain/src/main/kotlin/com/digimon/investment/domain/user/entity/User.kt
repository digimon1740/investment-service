package com.digimon.investment.domain.user.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.io.Serializable
import java.time.LocalDateTime

@Table("users")
data class User(
    @Id val id: Long,
    val username: String,
    val password: String,
    @CreatedDate val createdAt: LocalDateTime,
) : Serializable {

    companion object {
        private const val serialVersionUID = 1L
    }
}