package com.digimon.investment.api.web.v1.investment.response

import com.digimon.investment.domain.product.entity.Product
import com.digimon.investment.domain.user.entity.UserInvestment
import java.io.Serializable
import java.time.LocalDateTime

data class UserInvestResponse(
    val id: Long,
    val title: String,
    val totalInvestingAmount: Long = 0,
    val myInvestingAmount: Long = 0,
    val createdAt: LocalDateTime,
) : Serializable {

    companion object {
        private const val serialVersionUID = 1L

        fun of(userInvestment: UserInvestment, product: Product): UserInvestResponse {
            return UserInvestResponse(
                id = product.id,
                title = product.title,
                totalInvestingAmount = product.totalInvestingAmount,
                myInvestingAmount = userInvestment.investingAmount,
                createdAt = userInvestment.createdAt
            )
        }
    }
}