package com.digimon.investment.api.service.product.impl

import com.digimon.investment.api.service.product.ProductService
import com.digimon.investment.core.cache.InvestServiceCacheManager
import com.digimon.investment.core.cache.keys.CacheKeys
import com.digimon.investment.core.common.LoggerDelegate
import com.digimon.investment.core.exception.V1Exception
import com.digimon.investment.core.exception.V1Exception.*
import com.digimon.investment.domain.product.entity.Product
import com.digimon.investment.domain.product.repository.ProductRepository
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ProductServiceImpl(
    private val productRepository: ProductRepository,
    private val cacheManager: InvestServiceCacheManager,
) : ProductService {

    private val logger by LoggerDelegate()

    override suspend fun getAllOnSale(): List<Product> {
        val (key, ttl) = CacheKeys.getAllOnSale()
        return cacheManager.awaitGetOrPut(key, ttl) {
            val now = LocalDateTime.now()
            productRepository.findAllByStartedAtBeforeAndFinishedAtAfterOrderByStartedAt(now, now).toList()
        } ?: emptyList()
    }

    override suspend fun getByIdOnSale(id: Long): Product? {
        return productRepository.findByIdAndStartedAtBeforeAndFinishedAtAfter(id)
    }

    override suspend fun getAllByIds(ids: Collection<Long>): List<Product> {
        return productRepository.findAllById(ids).toList()
    }

    override suspend fun incrementInvestingAmount(id: Long, investingAmount: Long): Long {
        val product = getByIdOnSale(id) ?: throw V1Exception(Kind.PRODUCT, Status.NOT_FOUND)
        product.incrementCurrentInvestingAmount(investingAmount)
        val (key, ttl) = CacheKeys.getByIdOnSale(id)
        cacheManager.awaitPut(key, ttl) {
            productRepository.save(product)
        }
        return product.currentInvestingAmount
    }
}