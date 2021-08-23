package com.digimon.investment.domain.user.entity

import com.digimon.investment.core.exception.V1Exception
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.io.Serializable

@Table("user_balances")
data class UserBalance(
    @Id val id: Long? = null,
    val userId: Long,
    var balance: Long = 0,
) : Serializable {

    companion object {
        private const val serialVersionUID = 1L
    }

    fun throwIfUserBalanceIsNotEnough(investingAmount: Long) {
        if (investingAmount > balance) {
            throw V1Exception(V1Exception.Kind.USER, V1Exception.Status.NOT_ENOUGH)
        }
    }

    fun minus(withdrawalBalance: Long): Long {
        balance -= withdrawalBalance
        return balance
    }
}