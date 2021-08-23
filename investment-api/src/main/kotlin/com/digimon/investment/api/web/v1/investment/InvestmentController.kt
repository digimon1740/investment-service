package com.digimon.investment.api.web.v1.investment

import com.digimon.investment.api.service.investment.InvestmentService
import com.digimon.investment.api.web.v1.investment.request.InvestProductRequest
import com.digimon.investment.api.web.v1.investment.response.InvestProductResponse
import com.digimon.investment.api.web.v1.investment.response.UserInvestResponse
import com.digimon.investment.core.common.LoggerDelegate
import com.digimon.investment.core.model.v1.command.CreateInvestCommand
import com.digimon.investment.core.model.v1.response.MultiResponse
import com.digimon.investment.core.model.v1.response.SingleResponse
import com.digimon.investment.core.security.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/investments")
class InvestmentController(
    private val investmentService: InvestmentService,
) {

    private val logger by LoggerDelegate()

    @PostMapping
    suspend fun invest(
        userDetails: UserDetails,
        @RequestBody request: InvestProductRequest,
    ): SingleResponse<InvestProductResponse> {
        val command = CreateInvestCommand(
            userId = userDetails.userId,
            productId = request.productId,
            investingAmount = request.investingAmount
        )
        return SingleResponse(data = investmentService.invest(command))
    }

    @GetMapping
    suspend fun getAllByUser(
        userDetails: UserDetails,
    ): MultiResponse<UserInvestResponse> {
        return MultiResponse(data = investmentService.getAllByUserId(userDetails.userId))
    }
}