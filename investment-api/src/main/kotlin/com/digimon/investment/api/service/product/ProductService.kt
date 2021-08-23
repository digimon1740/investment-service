package com.digimon.investment.api.service.product

import com.digimon.investment.domain.product.entity.Product

interface ProductService {

    suspend fun getAllOnSale(): List<Product>

    suspend fun getByIdOnSale(id: Long): Product?

    suspend fun getAllByIds(ids: Collection<Long>): List<Product>

    suspend fun incrementInvestingAmount(id: Long, investingAmount: Long): Long
}