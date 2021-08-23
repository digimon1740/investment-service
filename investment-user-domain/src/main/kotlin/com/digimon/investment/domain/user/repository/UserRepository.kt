package com.digimon.investment.domain.user.repository

import com.digimon.investment.domain.user.entity.User
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface UserRepository : CoroutineCrudRepository<User, Long> {
}