package com.digimon.investment.api.service.user

import com.digimon.investment.domain.user.entity.UserInvestment

interface UserInvestmentService {

    suspend fun getAll(userId: Long): List<UserInvestment>

    suspend fun getByUserIdAndProductId(userId: Long,productId:Long): UserInvestment?

    suspend fun getAllByProductIds(productIds: Collection<Long>): Map<Long, List<UserInvestment>>

    suspend fun invest(userInvestment: UserInvestment): UserInvestment
}