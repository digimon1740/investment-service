package com.digimon.investment.api.web.v1.user.response

import com.digimon.investment.domain.user.entity.User
import java.time.LocalDateTime

data class MeResponse(
    val userId: Long,
    val username: String,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun of(user: User) =
            MeResponse(
                userId = user.id,
                username = user.username,
                createdAt = user.createdAt,
            )
    }
}