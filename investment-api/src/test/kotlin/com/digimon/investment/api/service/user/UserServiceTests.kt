package com.digimon.investment.api.service.user

import com.digimon.investment.api.service.user.impl.UserServiceImpl
import com.digimon.investment.domain.user.entity.User
import com.digimon.investment.domain.user.repository.UserRepository
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
class UserServiceTests {

    val userRepository = mock(UserRepository::class.java)

    @InjectMocks
    val userService = UserServiceImpl(userRepository)

    @Test
    fun `유저 조회`() = runBlocking<Unit> {
        // Given
        given(userRepository.findById(1)).willReturn(
            User(
                id = 1,
                username = "SangHoon Lee",
                password = "1234",
                createdAt = LocalDateTime.now()
            )
        )

        // When
        val user = userService.getById(1)

        // Then
        assertThat(user).isNotNull
        assertThat(user).extracting("username").isEqualTo("SangHoon Lee")
        assertThat(user).extracting("password").isEqualTo("1234")
    }
}