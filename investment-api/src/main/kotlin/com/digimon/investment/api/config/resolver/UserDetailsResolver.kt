package com.digimon.investment.api.config.resolver

import com.digimon.investment.api.service.user.UserService
import com.digimon.investment.core.exception.V1Exception
import com.digimon.investment.core.exception.V1Exception.Kind
import com.digimon.investment.core.exception.V1Exception.Status
import com.digimon.investment.core.security.UserDetails
import kotlinx.coroutines.reactor.mono
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.reactive.BindingContext
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class UserDetailsResolver(
    val userService: UserService,
) : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter) = UserDetails::class.java.isAssignableFrom(parameter.parameterType)

    override fun resolveArgument(parameter: MethodParameter, bindingContext: BindingContext, exchange: ServerWebExchange): Mono<Any> {
        return mono {
            val userId = exchange.request.headers["x-user-id"]?.firstOrNull()?.toLong()
                ?: throw V1Exception(Kind.USER, Status.UNAUTHORIZED)

            val user = userService.getById(userId) ?: throw V1Exception(Kind.USER, Status.UNAUTHORIZED)
            UserDetails(userId = userId, username = user.username)
        }
    }
}