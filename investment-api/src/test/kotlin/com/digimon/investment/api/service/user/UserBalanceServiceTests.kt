package com.digimon.investment.api.service.user

import com.digimon.investment.api.service.user.impl.UserBalanceServiceImpl
import com.digimon.investment.core.exception.V1Exception
import com.digimon.investment.domain.user.entity.UserBalance
import com.digimon.investment.domain.user.repository.UserBalanceRepository
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mockito.mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class UserBalanceServiceTests {

    val userBalanceRepository = mock(UserBalanceRepository::class.java)

    @InjectMocks
    val userBalanceService = UserBalanceServiceImpl(userBalanceRepository)

    @Test
    fun `유저 잔액 조회`() = runBlocking<Unit> {
        // Given
        given(userBalanceRepository.findByUserId(1)).willReturn(
            UserBalance(
                id = 1,
                userId = 1,
                balance = 1000,
            )
        )

        // When
        val actual = userBalanceService.getByUserId(1)

        // Then
        assertThat(actual).isNotNull
        assertThat(actual).extracting("userId").isEqualTo(1L)
        assertThat(actual).extracting("balance").isEqualTo(1000L)
    }

    @Test
    fun `투자가 완료되면 잔액이 차감되야한다`() = runBlocking<Unit> {
        // Given
        given(userBalanceRepository.findByUserId(1)).willReturn(UserBalance(id = 1, userId = 1, balance = 500))
        given(
            userBalanceRepository.save(UserBalance(id = 1, userId = 1, balance = 400))
        ).willReturn(
            UserBalance(id = 1, userId = 1, balance = 400)
        )

        // When
        val actual = userBalanceService.withdraw(userId = 1, withdrawalBalance = 100)

        // Then
        assertThat(actual).isGreaterThan(0)
        assertThat(actual).isEqualTo(400)
    }

    @Test
    fun `잔액이 부족하면 오류가 발생한다`() = runBlocking<Unit> {
        // Given
        given(userBalanceRepository.findByUserId(1)).willReturn(UserBalance(id = 1, userId = 1, balance = 100))

        assertThatThrownBy {
            runBlocking<Unit> {
                // When
                userBalanceService.withdraw(userId = 1, withdrawalBalance = 500)
            }
            // Then
        }.isInstanceOf(V1Exception::class.java)
    }
}