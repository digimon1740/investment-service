package com.digimon.investment.api.web.v1.user

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.digimon.investment.api.service.user.UserService
import com.digimon.investment.api.web.v1.user.response.MeResponse
import com.digimon.investment.core.model.v1.response.SingleResponse
import com.digimon.investment.domain.user.entity.User
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
import org.springframework.core.ParameterizedTypeReference
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
class UserControllerTests @Autowired constructor(
   private val objectMapper: ObjectMapper,
   private val webTestClient: WebTestClient
) {

    @MockBean
    lateinit var userService: UserService

    @Test
    fun `헤더에 있는 유저 아이디를 기준으로 유저 정보를 반환해야한다`() = runBlocking<Unit> {
        // Given
        val userId = 1L
        given(userService.getById(userId)).willReturn(
            User(
                id = userId,
                username = "SangHoon Lee",
                password = "1234",
                createdAt = LocalDateTime.now()
            )
        )

        // When
        val response = webTestClient.get()
            .uri("/api/v1/users/me")
            .accept(MediaType.APPLICATION_JSON)
            .header("X-USER-ID", "1")
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .consumeWith(
                document(
                    "users/me",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                )
            )
            .returnResult()
            .responseBody

        // Then
        val apiResponse = objectMapper.readValue(response, object : TypeReference<SingleResponse<MeResponse>>() {})
        assertThat(apiResponse).isNotNull

        val actual = apiResponse.data
        assertThat(actual).isNotNull
        assertThat(actual).extracting("userId").isEqualTo(userId)
        assertThat(actual).extracting("username").isEqualTo("SangHoon Lee")
    }

    @Test
    fun `헤더에 유저 아이디가 없는 경우 -10401 코드를 반환해야한다`() = runBlocking<Unit> {
        // Given
        val userId = 1L
        given(userService.getById(userId)).willReturn(
            User(
                id = userId,
                username = "SangHoon Lee",
                password = "1234",
                createdAt = LocalDateTime.now()
            )
        )

        // When
        val apiResponse = webTestClient.get()
            .uri("/api/v1/users/me")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody(object : ParameterizedTypeReference<SingleResponse<MeResponse>>() {})
            .returnResult()
            .responseBody

        // Then
        assertThat(apiResponse).isNotNull

        val responseCode = apiResponse?.code
        assertThat(responseCode).isEqualTo(-10401)
    }


}