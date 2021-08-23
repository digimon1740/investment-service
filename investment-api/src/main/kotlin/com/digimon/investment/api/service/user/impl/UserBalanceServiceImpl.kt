package com.digimon.investment.api.service.user.impl

import com.digimon.investment.api.service.user.UserBalanceService
import com.digimon.investment.core.common.LoggerDelegate
import com.digimon.investment.core.exception.V1Exception
import com.digimon.investment.core.exception.V1Exception.*
import com.digimon.investment.domain.user.entity.UserBalance
import com.digimon.investment.domain.user.repository.UserBalanceRepository
import org.springframework.stereotype.Service

@Service
class UserBalanceServiceImpl(
    private val userBalanceRepository: UserBalanceRepository,
) : UserBalanceService {

    private val logger by LoggerDelegate()

    override suspend fun getByUserId(userId: Long): UserBalance {
        return userBalanceRepository.findByUserId(userId)
    }

    override suspend fun withdraw(userId: Long, withdrawalBalance: Long): Long {
        val userBalance = getByUserId(userId)
        logger.info("userId : {}, prevBalance : {}, withdrawalBalance : {}", userId, userBalance.balance, withdrawalBalance)
        val remainBalance = userBalance.minus(withdrawalBalance)
        if (remainBalance <= 0) throw V1Exception(Kind.USER, Status.NOT_ENOUGH)
        userBalanceRepository.save(userBalance)
        return remainBalance
    }

}