package com.digimon.investment.api.service.user

import com.digimon.investment.api.service.user.impl.UserInvestmentServiceImpl
import com.digimon.investment.core.cache.InvestServiceCacheManager
import com.digimon.investment.core.cache.keys.CacheKeys
import com.digimon.investment.core.exception.V1Exception
import com.digimon.investment.domain.user.entity.UserBalance
import com.digimon.investment.domain.user.entity.UserInvestment
import com.digimon.investment.domain.user.repository.UserInvestmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mockito.mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class UserInvestmentServiceTests {

    val userInvestmentRepository = mock(UserInvestmentRepository::class.java)
    val cacheManager = mock(InvestServiceCacheManager::class.java)

    @InjectMocks
    val userInvestmentService = UserInvestmentServiceImpl(userInvestmentRepository, cacheManager)

    @Test
    fun `내가 투자한 상품 조회`() = runBlocking<Unit> {
        // Given
        given(userInvestmentRepository.findAllByUserId(1)).willReturn(
            listOf(
                UserInvestment(id = 1, userId = 1, productId = 3, investingAmount = 1000),
                UserInvestment(id = 2, userId = 1, productId = 4, investingAmount = 500)
            ).asFlow()
        )

        // When
        val actual = userInvestmentService.getAll(1)

        // Then
        assertThat(actual).isNotNull
        assertThat(actual.size).isEqualTo(2)
        assertThat(actual[0]).extracting("userId").isEqualTo(1L)
        assertThat(actual[0]).extracting("productId").isEqualTo(3L)
        assertThat(actual[0]).extracting("investingAmount").isEqualTo(1000L)

        assertThat(actual[1]).extracting("userId").isEqualTo(1L)
        assertThat(actual[1]).extracting("productId").isEqualTo(4L)
        assertThat(actual[1]).extracting("investingAmount").isEqualTo(500L)
    }

    @Test
    fun `내가 투자한 특정 상품 조회`() = runBlocking<Unit> {
        // Given
        given(userInvestmentRepository.findByUserIdAndProductId(1, 3)).willReturn(
            UserInvestment(
                id = 1,
                userId = 1,
                productId = 3,
                investingAmount = 1000
            )
        )

        // When
        val actual = userInvestmentService.getByUserIdAndProductId(1, 3)

        // Then
        assertThat(actual).isNotNull
        assertThat(actual).extracting("userId").isEqualTo(1L)
        assertThat(actual).extracting("productId").isEqualTo(3L)
        assertThat(actual).extracting("investingAmount").isEqualTo(1000L)
    }

    @Test
    fun `투자 내역 저장`() = runBlocking<Unit> {
        // Given
        val userInvestment = UserInvestment(
            userId = 1,
            productId = 3,
            investingAmount = 1000
        )
        given(userInvestmentRepository.save(userInvestment)).willReturn(userInvestment.copy(id = 1))

        // When
        val actual = userInvestmentService.invest(userInvestment)

        // Then
        assertThat(actual).isNotNull
        assertThat(actual).extracting("userId").isEqualTo(1L)
        assertThat(actual).extracting("productId").isEqualTo(3L)
        assertThat(actual).extracting("investingAmount").isEqualTo(1000L)
    }
}