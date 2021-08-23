package com.digimon.investment.api.service.investment

import com.digimon.investment.api.service.investment.impl.InvestmentServiceImpl
import com.digimon.investment.api.service.product.ProductService
import com.digimon.investment.api.service.user.UserBalanceService
import com.digimon.investment.api.service.user.UserInvestmentService
import com.digimon.investment.core.exception.V1Exception
import com.digimon.investment.core.exception.V1Exception.Kind
import com.digimon.investment.core.exception.V1Exception.Status
import com.digimon.investment.core.model.v1.command.CreateInvestCommand
import com.digimon.investment.domain.product.entity.Product
import com.digimon.investment.domain.product.entity.value.ProductType
import com.digimon.investment.domain.user.entity.UserBalance
import com.digimon.investment.domain.user.entity.UserInvestment
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mockito.mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.transaction.ReactiveTransactionManager
import java.time.LocalDateTime

//@SpringBootTest
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@ExtendWith(*[SpringExtension::class, MockitoExtension::class])
@ExtendWith(*[MockitoExtension::class])
class InvestmentServiceTests {

    val productService = mock(ProductService::class.java)
    val userInvestmentService = mock(UserInvestmentService::class.java)
    val userBalanceService = mock(UserBalanceService::class.java)
    val reactiveTransactionManager = mock(ReactiveTransactionManager::class.java)

    @InjectMocks
    val investmentService = InvestmentServiceImpl(
        productService,
        userInvestmentService,
        userBalanceService
    )

    @Test
    fun `내가 투자한 모든 상품 가져오기`() = runBlocking<Unit> {
        // Given
        val userId = 1L
        given(userInvestmentService.getAll(userId)).willReturn(
            listOf(
                UserInvestment(id = 1, userId = 1, productId = 3, investingAmount = 1000),
                UserInvestment(id = 2, userId = 1, productId = 4, investingAmount = 500)
            )
        )
        given(productService.getAllByIds(listOf(3, 4))).willReturn(getProducts())

        // When
        val actual = investmentService.getAllByUserId(userId)

        // Then
        assertThat(actual).isNotNull
        assertThat(actual.size).isEqualTo(2)
        assertThat(actual[0]).extracting("id").isEqualTo(3L)
        assertThat(actual[0]).extracting("title").isEqualTo("한국인덱스펀드")
        assertThat(actual[0]).extracting("totalInvestingAmount").isEqualTo(500000L)
        assertThat(actual[0]).extracting("myInvestingAmount").isEqualTo(1000L)

        assertThat(actual[1]).extracting("id").isEqualTo(4L)
        assertThat(actual[1]).extracting("title").isEqualTo("서울부동산")
        assertThat(actual[1]).extracting("totalInvestingAmount").isEqualTo(300000L)
        assertThat(actual[1]).extracting("myInvestingAmount").isEqualTo(500L)
    }

    @Test
    fun `투자하기`() = runBlocking<Unit> {
        // Given
        val userId = 1L
        val productId = 3L
        given(productService.getByIdOnSale(productId)).willReturn(getProduct())
        given(userBalanceService.getByUserId(userId)).willReturn(getUserBalance())
        given(userInvestmentService.getByUserIdAndProductId(userId, productId)).willReturn(null)

        // When
        val command = CreateInvestCommand(
            userId = userId,
            productId = productId,
            investingAmount = 1000L
        )
        val actual = investmentService.invest(command)

        // Then
        assertThat(actual).isNotNull
        assertThat(actual).extracting("id").isEqualTo(3L)
        assertThat(actual).extracting("title").isEqualTo("한국인덱스펀드")
        assertThat(actual).extracting("totalInvestingAmount").isEqualTo(500000L)
        assertThat(actual).extracting("currentInvestingAmount").isEqualTo(3000L)
    }

    @Test
    fun `상품이 존재하지 않거나 투자기간이 아니면 오류를 발생시킨다`() = runBlocking<Unit> {
        // Given
        val userId = 1L
        val productId = 3L
        given(productService.getByIdOnSale(productId))
            .willThrow(V1Exception(Kind.PRODUCT, Status.NOT_FOUND))

        // When
        val command = CreateInvestCommand(
            userId = userId,
            productId = productId,
            investingAmount = 1000L
        )
        assertThatThrownBy {
            runBlocking<Unit> {
                investmentService.invest(command)
            }
        }.isInstanceOf(V1Exception::class.java)
    }

    @Test
    fun `유저의 잔고가 충분하지 않은 경우 오류를 발생시킨다`() = runBlocking<Unit> {
        // Given
        val userId = 1L
        val productId = 3L
        given(productService.getByIdOnSale(productId))
            .willReturn(getProduct())
        given(userBalanceService.getByUserId(userId))
            .willReturn(getUserBalance().copy(balance = 0))
        // When
        val command = CreateInvestCommand(
            userId = userId,
            productId = productId,
            investingAmount = 1000L
        )
        assertThatThrownBy {
            runBlocking<Unit> {
                investmentService.invest(command)
            }
        }.isInstanceOf(V1Exception::class.java)
    }

    @Test
    fun `이미 투자한 상품의 경우 오류를 발생시킨다`() = runBlocking<Unit> {
        // Given
        val userId = 1L
        val productId = 3L
        given(productService.getByIdOnSale(productId)).willReturn(getProduct())
        given(userBalanceService.getByUserId(userId)).willReturn(getUserBalance())
        given(userInvestmentService.getByUserIdAndProductId(userId, productId))
            .willReturn(getUserInvestment())

        // When
        val command = CreateInvestCommand(
            userId = userId,
            productId = productId,
            investingAmount = 1000L
        )
        assertThatThrownBy {
            runBlocking<Unit> {
                investmentService.invest(command)
            }
        }.isInstanceOf(V1Exception::class.java)
    }

    @Test
    fun `완판된 상품의 경우 오류를 발생시킨다`() = runBlocking<Unit> {
        // Given
        val userId = 1L
        val productId = 3L
        val productStub = getProduct()
        given(productService.getByIdOnSale(productId))
            .willReturn(
                productStub.copy(currentInvestingAmount = productStub.totalInvestingAmount + 1000)
            )
        given(userBalanceService.getByUserId(userId)).willReturn(getUserBalance())
        given(userInvestmentService.getByUserIdAndProductId(userId, productId))
            .willReturn(null)

        // When
        val command = CreateInvestCommand(
            userId = userId,
            productId = productId,
            investingAmount = 1000L
        )
        assertThatThrownBy {
            runBlocking<Unit> {
                investmentService.invest(command)
            }
        }.isInstanceOf(V1Exception::class.java)
    }

    fun getProducts() = listOf(
        Product(
            id = 3L,
            title = "한국인덱스펀드",
            type = ProductType.CREDIT,
            totalInvestingAmount = 500000,
            currentInvestingAmount = 3000,
            version = 1,
            startedAt = LocalDateTime.of(2021, 5, 1, 0, 0, 0),
            finishedAt = LocalDateTime.of(2021, 10, 1, 0, 0, 0),
            createdAt = LocalDateTime.of(2021, 4, 1, 0, 0, 0),
            updatedAt = LocalDateTime.of(2021, 4, 1, 0, 0, 0),
        ),
        Product(
            id = 4,
            title = "서울부동산",
            type = ProductType.PROPERTY,
            totalInvestingAmount = 300000,
            currentInvestingAmount = 5000,
            version = 1,
            startedAt = LocalDateTime.of(2021, 8, 1, 0, 0, 0),
            finishedAt = LocalDateTime.of(2021, 11, 1, 0, 0, 0),
            createdAt = LocalDateTime.of(2021, 7, 1, 0, 0, 0),
            updatedAt = LocalDateTime.of(2021, 7, 1, 0, 0, 0),
        )
    )

    fun getProduct() = Product(
        id = 3L,
        title = "한국인덱스펀드",
        type = ProductType.CREDIT,
        totalInvestingAmount = 500000,
        currentInvestingAmount = 3000,
        version = 1,
        startedAt = LocalDateTime.of(2021, 5, 1, 0, 0, 0),
        finishedAt = LocalDateTime.of(2021, 10, 1, 0, 0, 0),
        createdAt = LocalDateTime.of(2021, 4, 1, 0, 0, 0),
        updatedAt = LocalDateTime.of(2021, 4, 1, 0, 0, 0),
    )

    fun getUserBalance() = UserBalance(
        id = 1,
        userId = 1,
        balance = 1000,
    )

    fun getUserInvestment() = UserInvestment(id = 1, userId = 1, productId = 3, investingAmount = 1000)
}