package com.digimon.investment.api.service.user.impl

import com.digimon.investment.api.service.user.UserInvestmentService
import com.digimon.investment.core.cache.InvestServiceCacheManager
import com.digimon.investment.core.cache.keys.CacheKeys
import com.digimon.investment.core.common.LoggerDelegate
import com.digimon.investment.domain.user.entity.UserInvestment
import com.digimon.investment.domain.user.repository.UserInvestmentRepository
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service

@Service
class UserInvestmentServiceImpl(
    private val userInvestmentRepository: UserInvestmentRepository,
    private val cacheManager: InvestServiceCacheManager,
) : UserInvestmentService {

    private val logger by LoggerDelegate()

    override suspend fun getAll(userId: Long): List<UserInvestment> {
        return userInvestmentRepository.findAllByUserId(userId).toList()
    }

    override suspend fun getByUserIdAndProductId(userId: Long, productId: Long): UserInvestment? {
        return userInvestmentRepository.findByUserIdAndProductId(userId, productId)
    }

    override suspend fun getAllByProductIds(productIds: Collection<Long>): Map<Long, List<UserInvestment>> {
        val (key, ttl) = CacheKeys.getAllByProductIds(productIds)
        val cached = cacheManager.awaitGetOrPutNotNull(key, ttl) {
            val userInvestments = userInvestmentRepository.findAllByProductIdIn(productIds)
            logger.info("userInvestments : {}", userInvestments)
            userInvestments.toList().groupBy(UserInvestment::productId)
        }
        logger.info("cached : {}", cached)
        return cached
    }

    override suspend fun invest(userInvestment: UserInvestment): UserInvestment {
        return userInvestmentRepository.save(userInvestment)
    }

}