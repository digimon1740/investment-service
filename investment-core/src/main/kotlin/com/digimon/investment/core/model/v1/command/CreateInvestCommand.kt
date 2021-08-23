package com.digimon.investment.core.model.v1.command

data class CreateInvestCommand(
    val userId: Long,
    val productId: Long,
    val investingAmount: Long
)