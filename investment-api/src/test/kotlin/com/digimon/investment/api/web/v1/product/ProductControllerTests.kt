package com.digimon.investment.api.web.v1.product

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.digimon.investment.api.service.product.ProductService
import com.digimon.investment.api.service.user.UserInvestmentService
import com.digimon.investment.api.web.v1.product.response.ProductOnSaleResponse
import com.digimon.investment.core.model.v1.response.MultiResponse
import com.digimon.investment.domain.product.entity.Product
import com.digimon.investment.domain.product.entity.value.ProductType
import com.digimon.investment.domain.user.entity.UserInvestment
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.LocalDateTime

@SpringBootTest
@AutoConfigureRestDocs
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(*[RestDocumentationExtension::class, SpringExtension::class])
class ProductControllerTests @Autowired constructor(
    private val objectMapper: ObjectMapper,
    private val webTestClient: WebTestClient
) {

    @MockBean
    lateinit var productService: ProductService

    @MockBean
    lateinit var userInvestmentService: UserInvestmentService

    @Test
    fun `상품 모집기간내의 전체 투자 상품을 조회한다`() = runBlocking<Unit> {
        // Given
        given(productService.getAllOnSale()).willReturn(getProducts())
        given(userInvestmentService.getAllByProductIds(listOf(3, 4))).willReturn(getUserInvestmentMap())

        // When
        val response = webTestClient.get()
            .uri("/api/v1/products")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .consumeWith(
                document(
                    "products",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                )
            )
            .returnResult()
            .responseBody

        // Then
        val apiResponse = objectMapper.readValue(response, object : TypeReference<MultiResponse<ProductOnSaleResponse>>() {})
        assertThat(apiResponse).isNotNull

        val actual = apiResponse.data
        assertThat(actual).isNotNull
        assertThat(actual.size).isGreaterThan(0)
        assertThat(actual[0]).extracting("title").isEqualTo("한국인덱스펀드")
        assertThat(actual[0]).extracting("totalInvestingAmount")
        assertThat(actual[0]).extracting("totalInvestingAmount").isEqualTo(500000L)

        assertThat(actual[1]).extracting("title").isEqualTo("서울부동산")
        assertThat(actual[1]).extracting("totalInvestingAmount").isEqualTo(300000L)
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

    fun getUserInvestmentMap() =
        HashMap<Long, List<UserInvestment>>().apply {
            val list1 = listOf(
                UserInvestment(
                    id = 1,
                    userId = 1,
                    productId = 3,
                    investingAmount = 3000,
                    createdAt = LocalDateTime.of(2021, 5, 1, 0, 1, 0),
                )
            )
            val list2 = listOf(
                UserInvestment(
                    id = 2,
                    userId = 1,
                    productId = 4,
                    investingAmount = 3000,
                    createdAt = LocalDateTime.of(2021, 8, 1, 0, 1, 0),
                ),
                UserInvestment(
                    id = 3,
                    userId = 2,
                    productId = 4,
                    investingAmount = 2000,
                    createdAt = LocalDateTime.of(2021, 8, 1, 0, 5, 0),
                )
            )
            put(3L, list1)
            put(4L, list2)
        }
}