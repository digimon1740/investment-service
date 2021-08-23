package com.digimon.investment.api.service.product

import com.digimon.investment.api.service.product.impl.ProductServiceImpl
import com.digimon.investment.core.cache.InvestServiceCacheManager
import com.digimon.investment.domain.product.entity.Product
import com.digimon.investment.domain.product.entity.value.ProductType
import com.digimon.investment.domain.product.repository.ProductRepository
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mockito.mock
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class ProductServiceTests {

    val productRepository = mock(ProductRepository::class.java)

    val cacheManager = mock(InvestServiceCacheManager::class.java)

    @InjectMocks
    val productService = ProductServiceImpl(productRepository, cacheManager)

    @Test
    fun `상품 아이디와 판매 기간을 받아서 상품 조회`() = runBlocking<Unit> {
        // Given
        val product = getProduct()
        given(productRepository.findByIdAndStartedAtBeforeAndFinishedAtAfter(product.id)).willReturn(product)
        // When
        val actual = productService.getByIdOnSale(3)

        // Then
        assertThat(actual).isNotNull
        assertThat(actual).extracting("title").isEqualTo(product.title)
        assertThat(actual).extracting("totalInvestingAmount").isEqualTo(product.totalInvestingAmount)
        assertThat(actual).extracting("currentInvestingAmount").isEqualTo(product.currentInvestingAmount)
        assertThat(actual).extracting("startedAt").isEqualTo(product.startedAt)
        assertThat(actual).extracting("finishedAt").isEqualTo(product.finishedAt)
    }

    @Test
    fun `투자 모집 금액 증가`() = runBlocking<Unit> {
        // Given
        val product = getProduct()
        given(productService.getByIdOnSale(3)).willReturn(product)

        // When
        val myInvestingAmount = 1000L
        val actual = productService.incrementInvestingAmount(3, myInvestingAmount)

        // Then
        assertThat(actual).isNotNull
        assertThat(actual).isEqualTo(4000L)
    }

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
}