package com.digimon.investment.domain.user.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalDateTime

@Table("user_investments")
data class UserInvestment(
    @Id val id: Long = 0,
    val userId: Long,
    val productId: Long,
    val investingAmount: Long,
    @CreatedDate val createdAt: LocalDateTime = LocalDateTime.now(),
) : Serializable {

    companion object {
        private const val serialVersionUID = 1L
    }

}