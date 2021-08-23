package com.digimon.investment.domain.product.entity

import com.digimon.investment.domain.product.entity.value.ProductType
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table
import java.io.Serializable
import java.time.LocalDateTime

@Table("products")
data class Product(
    @Id val id: Long,
    val title: String,
    val type: ProductType,
    val totalInvestingAmount: Long,
    var currentInvestingAmount: Long,
    @Version val version: Long, // for optimistic locking
    val startedAt: LocalDateTime,
    val finishedAt: LocalDateTime,
    @CreatedDate val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) : Serializable {

    companion object {
        private const val serialVersionUID = 1L
    }

    fun incrementCurrentInvestingAmount(investingAmount: Long) {
        currentInvestingAmount += investingAmount
    }

    val isSoldOut: Boolean
        get() = totalInvestingAmount < currentInvestingAmount

}