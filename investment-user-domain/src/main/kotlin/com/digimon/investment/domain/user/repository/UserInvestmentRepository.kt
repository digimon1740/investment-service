package com.digimon.investment.domain.user.repository

import com.digimon.investment.domain.user.entity.UserInvestment
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface UserInvestmentRepository : CoroutineCrudRepository<UserInvestment, Long> {

    suspend fun findAllByUserId(userId: Long): Flow<UserInvestment>

    suspend fun findByUserIdAndProductId(userId: Long, productId: Long): UserInvestment?

    suspend fun findAllByProductIdIn(productIds: Collection<Long>): Flow<UserInvestment>

    suspend fun findAllByProductId(productId: Long): Flow<UserInvestment>
}