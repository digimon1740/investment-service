package com.digimon.investment.api.web.v1.user

import com.digimon.investment.api.service.user.UserService
import com.digimon.investment.api.web.v1.user.response.MeResponse
import com.digimon.investment.core.model.v1.response.SingleResponse
import com.digimon.investment.core.security.UserDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService,
) {

    @GetMapping("/me")
    suspend fun me(
        userDetails: UserDetails,
    ) = SingleResponse(data = MeResponse.of(userService.getById(userDetails.userId)!!))

    // TODO balance API

}