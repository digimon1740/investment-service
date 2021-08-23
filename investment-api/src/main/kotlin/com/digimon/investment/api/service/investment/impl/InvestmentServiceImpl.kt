package com.digimon.investment.api.service.investment.impl

import com.digimon.investment.api.service.investment.InvestmentService
import com.digimon.investment.api.service.product.ProductService
import com.digimon.investment.api.service.user.UserBalanceService
import com.digimon.investment.api.service.user.UserInvestmentService
import com.digimon.investment.api.web.v1.investment.response.InvestProductResponse
import com.digimon.investment.api.web.v1.investment.response.UserInvestResponse
import com.digimon.investment.core.common.LoggerDelegate
import com.digimon.investment.core.exception.V1Exception
import com.digimon.investment.core.exception.V1Exception.Kind
import com.digimon.investment.core.exception.V1Exception.Status
import com.digimon.investment.core.model.v1.command.CreateInvestCommand
import com.digimon.investment.domain.user.entity.UserInvestment
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait

@Service
class InvestmentServiceImpl(
    private val productService: ProductService,
    private val userInvestmentService: UserInvestmentService,
    private val userBalanceService: UserBalanceService,
) : InvestmentService {

    private val logger by LoggerDelegate()

    override suspend fun getAllByUserId(userId: Long): List<UserInvestResponse> {
        val userInvestments = userInvestmentService.getAll(userId)
        val productIds = userInvestments.map(UserInvestment::productId)
        val productMap = productService.getAllByIds(productIds).associateBy { it.id }

        return userInvestments.map { userInvestment ->
            val product = productMap[userInvestment.productId]!!
            UserInvestResponse.of(userInvestment, product)
        }
    }

    @Transactional
    override suspend fun invest(command: CreateInvestCommand): InvestProductResponse? {
        // 1. 존재하는 상품이면서 투자기간내의 상품인지 확인
        productService.getByIdOnSale(command.productId) ?: throw V1Exception(Kind.PRODUCT, Status.NOT_FOUND)

        // 2. 유저의 잔고가 충분한지 확인
        val userBalance = userBalanceService.getByUserId(command.userId)
        userBalance.throwIfUserBalanceIsNotEnough(command.investingAmount)

        // 3. 이미 투자한 상품인지 확인한다.
        val userInvestment = userInvestmentService.getByUserIdAndProductId(command.userId, command.productId)
        if (userInvestment != null) {
            throw V1Exception(Kind.INVESTMENT, Status.ALREADY_INVESTED)
        }

        // TODO Redis 스핀 락 또는 Redisson pub/sub 기반의 분산 락도 고려 가능
        // 현재는 version 기반의 낙관적 락을 적용

        // Begin Transaction
        // 투자 이력을 생성한다.
        userInvestmentService.invest(
            UserInvestment(
                userId = command.userId,
                productId = command.productId,
                investingAmount = command.investingAmount
            )
        )
        // 잔고에서 투자금을 출금한다.
        userBalanceService.withdraw(command.userId, command.investingAmount)

        // 완판된 상태라면 sold_out 오류를 발생시키고 롤백
        productService.incrementInvestingAmount(command.productId, command.investingAmount)
        val product = productService.getByIdOnSale(command.productId)!!
        logger.info(
            "totalInvestingAmount : {}, totalCurrentInvestingAmount : {}",
            product.totalInvestingAmount,
            product.currentInvestingAmount
        )
        if (product.isSoldOut) {
            throw V1Exception(Kind.INVESTMENT, Status.SOLD_OUT)
        }
        return InvestProductResponse.of(product)
        // End Transaction
    }

}