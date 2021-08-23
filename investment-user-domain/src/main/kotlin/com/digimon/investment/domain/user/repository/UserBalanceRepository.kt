package com.digimon.investment.domain.user.repository

import com.digimon.investment.domain.user.entity.UserBalance
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface UserBalanceRepository : CoroutineCrudRepository<UserBalance, Long> {

    suspend fun findByUserId(userId: Long): UserBalance
}