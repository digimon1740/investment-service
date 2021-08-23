package com.digimon.investment.api.service.user

import com.digimon.investment.domain.user.entity.User

interface UserService {

    suspend fun getById(id: Long): User?
}