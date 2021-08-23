package com.digimon.investment.api.web.v1.investment.response

import com.digimon.investment.domain.product.entity.Product
import com.digimon.investment.domain.product.entity.value.ProductStatus
import java.io.Serializable
import java.time.LocalDateTime

data class InvestProductResponse(
    val id: Long,
    val title: String,
    val totalInvestingAmount: Long = 0,
    val currentInvestingAmount: Long = 0,
    val status: ProductStatus = if (totalInvestingAmount >= currentInvestingAmount) ProductStatus.ON_SALE else ProductStatus.SOLD_OUT,
    val startedAt: LocalDateTime,
    val finishedAt: LocalDateTime,
) : Serializable {

    companion object {
        private const val serialVersionUID = 1L

        fun of(product: Product): InvestProductResponse {
            return InvestProductResponse(
                id = product.id,
                title = product.title,
                totalInvestingAmount = product.totalInvestingAmount,
                currentInvestingAmount = product.currentInvestingAmount,
                startedAt = product.startedAt,
                finishedAt = product.finishedAt
            )
        }
    }
}