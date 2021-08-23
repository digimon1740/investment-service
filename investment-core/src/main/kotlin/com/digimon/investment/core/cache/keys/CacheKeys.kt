package com.digimon.investment.core.cache.keys

import java.time.Duration

object CacheKeys {

    fun getAllOnSale() =
        Pair("product:getAllOnSale:v1", Duration.ofMinutes(1))

    fun getByIdOnSale(productId: Long) =
        Pair("product:getByIdOnSale:v1:${productId}", Duration.ofMinutes(1))

    fun getAllByProductIds(productIds: Collection<Long>) =
        Pair("userInvestment:getAllByProductIds:v1:${productIds.joinToString(",")}", Duration.ofMinutes(1))

    fun getAllByProductId(productId: Long) =
        Pair("userInvestment:getAllByProductId:v1:${productId}", Duration.ofMinutes(1))
}