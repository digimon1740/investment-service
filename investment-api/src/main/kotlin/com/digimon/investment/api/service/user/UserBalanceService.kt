package com.digimon.investment.api.service.user

import com.digimon.investment.domain.user.entity.UserBalance

interface UserBalanceService {

    suspend fun getByUserId(userId: Long): UserBalance

    suspend fun withdraw(userId: Long, withdrawalBalance: Long): Long
}
