package com.digimon.investment.api.service.user.impl

import com.digimon.investment.api.service.user.UserService
import com.digimon.investment.domain.user.entity.User
import com.digimon.investment.domain.user.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
    private val userRepository: UserRepository
) : UserService {

    override suspend fun getById(id: Long): User? {
        return userRepository.findById(id)
    }
}