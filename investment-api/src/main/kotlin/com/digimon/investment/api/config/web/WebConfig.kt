package com.digimon.investment.api.config.web

import com.digimon.investment.api.config.resolver.UserDetailsResolver
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer

@Configuration
class WebConfig(
    val userDetailsResolver: UserDetailsResolver,
) : WebFluxConfigurer {


    override fun configureArgumentResolvers(configurer: ArgumentResolverConfigurer) {
        super.configureArgumentResolvers(configurer)
        configurer.addCustomResolver(userDetailsResolver)
    }
}