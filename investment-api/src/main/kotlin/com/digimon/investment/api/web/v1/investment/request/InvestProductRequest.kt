package com.digimon.investment.api.web.v1.investment.request

data class InvestProductRequest(
    val productId: Long,
    val investingAmount: Long
)