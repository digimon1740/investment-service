package com.digimon.investment.api.web.v1.investment

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.digimon.investment.api.service.investment.InvestmentService
import com.digimon.investment.api.service.user.UserBalanceService
import com.digimon.investment.api.web.v1.investment.request.InvestProductRequest
import com.digimon.investment.api.web.v1.investment.response.InvestProductResponse
import com.digimon.investment.api.web.v1.investment.response.UserInvestResponse
import com.digimon.investment.core.exception.V1Exception
import com.digimon.investment.core.exception.V1Exception.*
import com.digimon.investment.core.model.v1.command.CreateInvestCommand
import com.digimon.investment.core.model.v1.response.ErrorResponse
import com.digimon.investment.core.model.v1.response.MultiResponse
import com.digimon.investment.core.model.v1.response.SingleResponse
import com.digimon.investment.domain.product.entity.value.ProductStatus
import com.digimon.investment.domain.user.entity.UserBalance
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
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.LocalDateTime

@SpringBootTest
@AutoConfigureRestDocs
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(*[RestDocumentationExtension::class, SpringExtension::class])
class InvestmentControllerTests @Autowired constructor(
    private val objectMapper: ObjectMapper,
    private val webTestClient: WebTestClient
) {

    @MockBean
    lateinit var investmentService: InvestmentService

    @MockBean
    lateinit var userBalanceService: UserBalanceService

    fun getCreateCommand() = CreateInvestCommand(
        userId = 1,
        productId = 5,
        investingAmount = 1111,
    )

    @Test
    fun `내가 투자한 상품을 조회한다`() = runBlocking<Unit> {
        // Given
        given(investmentService.getAllByUserId(1)).willReturn(
            listOf(
                UserInvestResponse(
                    id = 3,
                    title = "한국인덱스펀드",
                    totalInvestingAmount = 500000,
                    myInvestingAmount = 3000,
                    createdAt = LocalDateTime.of(2021, 5, 1, 0, 1, 0),
                ),
                UserInvestResponse(
                    id = 4,
                    title = "서울부동산",
                    totalInvestingAmount = 300000,
                    myInvestingAmount = 3000,
                    createdAt = LocalDateTime.of(2021, 8, 1, 0, 1, 0),
                )
            )
        )

        // When
        val response = webTestClient.get()
            .uri("/api/v1/investments")
            .header("X-USER-ID", "1")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .consumeWith(
                document(
                    "investments/get-all-by-user",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                )
            )
            .returnResult()
            .responseBody

        // Then
        val apiResponse = objectMapper.readValue(response, object : TypeReference<MultiResponse<UserInvestResponse>>() {})
        assertThat(apiResponse).isNotNull

        val actual = apiResponse.data
        assertThat(actual).isNotNull
        assertThat(actual.size).isGreaterThan(0)
        assertThat(actual[0]).extracting("title").isEqualTo("한국인덱스펀드")
        assertThat(actual[0]).extracting("totalInvestingAmount").isEqualTo(500000L)
        assertThat(actual[0]).extracting("myInvestingAmount").isEqualTo(3000L)

        assertThat(actual[1]).extracting("title").isEqualTo("서울부동산")
        assertThat(actual[1]).extracting("totalInvestingAmount").isEqualTo(300000L)
        assertThat(actual[1]).extracting("myInvestingAmount").isEqualTo(3000L)
    }

    @Test
    fun `정상적으로 투자가 이뤄져야한다`() = runBlocking<Unit> {
        // Given
        val command = getCreateCommand()
        given(investmentService.invest(command)).willReturn(
            InvestProductResponse(
                id = command.productId,
                title = "한국인덱스펀드",
                totalInvestingAmount = 500000,
                currentInvestingAmount = command.investingAmount,
                status = ProductStatus.ON_SALE,
                startedAt = LocalDateTime.of(2021, 5, 1, 0, 0, 0),
                finishedAt = LocalDateTime.of(2021, 10, 1, 0, 0, 0),
            )
        )

        // When
        val response = webTestClient.post()
            .uri("/api/v1/investments")
            .header("X-USER-ID", "1")
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(InvestProductRequest(productId = 5, investingAmount = 1111))
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .consumeWith(
                document(
                    "investments/post-invest",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("productId").description("투자 상품 아이디"),
                        fieldWithPath("investingAmount").description("투자 금액"),
                    ),
                )
            )
            .returnResult()
            .responseBody

        // Then
        val apiResponse = objectMapper.readValue(response, object : TypeReference<SingleResponse<InvestProductResponse>>() {})
        assertThat(apiResponse).isNotNull

        val actual = apiResponse.data
        assertThat(actual).isNotNull
        assertThat(actual).extracting("title").isEqualTo("한국인덱스펀드")
        assertThat(actual).extracting("totalInvestingAmount").isEqualTo(500000L)
        assertThat(actual).extracting("currentInvestingAmount").isEqualTo(1111L)
        assertThat(actual).extracting("status").isEqualTo(ProductStatus.ON_SALE)
    }

    @Test
    fun `존재하지 않는 상품은 -11404 코드를 반환한다`() = runBlocking<Unit> {
        // Given
        val command = getCreateCommand()
        given(investmentService.invest(command)).willThrow(V1Exception(Kind.PRODUCT, Status.NOT_FOUND))

        // When
        val response = webTestClient.post()
            .uri("/api/v1/investments")
            .header("X-USER-ID", "1")
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(InvestProductRequest(productId = 5, investingAmount = 1111))
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .consumeWith(
                document(
                    "investments/post-invest-error-11404",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                )
            )
            .returnResult()
            .responseBody

        // Then
        val apiResponse = objectMapper.readValue(response, ErrorResponse::class.java)
        assertThat(apiResponse).isNotNull
        assertThat(apiResponse.code).isEqualTo(-11404)
    }

    @Test
    fun `사용자의 잔고가 충분하지 않다면 -10601 코드를 반환한다`() = runBlocking<Unit> {
        // Given
        val command = getCreateCommand()
        given(userBalanceService.getByUserId(1)).willReturn(UserBalance(id = 1, userId = 1, balance = 0))
        given(investmentService.invest(command)).willThrow(V1Exception(Kind.USER, Status.NOT_ENOUGH))

        // When
        val response = webTestClient.post()
            .uri("/api/v1/investments")
            .header("X-USER-ID", "1")
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(InvestProductRequest(productId = 5, investingAmount = 1111))
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .consumeWith(
                document(
                    "investments/post-invest-error-10601",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                )
            )
            .returnResult()
            .responseBody

        // Then
        val apiResponse = objectMapper.readValue(response, ErrorResponse::class.java)
        assertThat(apiResponse).isNotNull
        assertThat(apiResponse.code).isEqualTo(-10601)
    }

    @Test
    fun `이미 투자한 상품이라면 -12603 코드를 반환한다`() = runBlocking<Unit> {
        // Given
        val command = getCreateCommand()
        given(investmentService.invest(command)).willThrow(V1Exception(Kind.INVESTMENT, Status.ALREADY_INVESTED))

        // When
        val response = webTestClient.post()
            .uri("/api/v1/investments")
            .header("X-USER-ID", "1")
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(InvestProductRequest(productId = 5, investingAmount = 1111))
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .consumeWith(
                document(
                    "investments/post-invest-error-12603",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                )
            )
            .returnResult()
            .responseBody

        // Then
        val apiResponse = objectMapper.readValue(response, ErrorResponse::class.java)
        assertThat(apiResponse).isNotNull
        assertThat(apiResponse.code).isEqualTo(-12603)
    }

    @Test
    fun `완판된 상품이라면 -12602 코드를 반환한다`() = runBlocking<Unit> {
        // Given
        val command = getCreateCommand()
        given(investmentService.invest(command)).willThrow(V1Exception(Kind.INVESTMENT, Status.SOLD_OUT))

        // When
        val response = webTestClient.post()
            .uri("/api/v1/investments")
            .header("X-USER-ID", "1")
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(InvestProductRequest(productId = 5, investingAmount = 1111))
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .consumeWith(
                document(
                    "investments/post-invest-error-12602",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                )
            )
            .returnResult()
            .responseBody

        // Then
        val apiResponse = objectMapper.readValue(response, ErrorResponse::class.java)
        assertThat(apiResponse).isNotNull
        assertThat(apiResponse.code).isEqualTo(-12602)
    }
}