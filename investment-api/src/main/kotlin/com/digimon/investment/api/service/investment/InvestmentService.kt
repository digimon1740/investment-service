package com.digimon.investment.api.service.investment

import com.digimon.investment.api.web.v1.investment.response.InvestProductResponse
import com.digimon.investment.api.web.v1.investment.response.UserInvestResponse
import com.digimon.investment.core.model.v1.command.CreateInvestCommand

interface InvestmentService {

    suspend fun getAllByUserId(userId: Long): List<UserInvestResponse>

    suspend fun invest(command: CreateInvestCommand): InvestProductResponse?
}
