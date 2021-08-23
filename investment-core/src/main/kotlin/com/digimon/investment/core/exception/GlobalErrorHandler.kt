package com.digimon.investment.core.exception

import com.fasterxml.jackson.databind.ObjectMapper
import com.digimon.investment.core.common.LoggerDelegate
import com.digimon.investment.core.model.v1.response.ErrorResponse
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.mono
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Configuration
@Order(-2)
class GlobalErrorHandler(private val objectMapper: ObjectMapper) : ErrorWebExceptionHandler {

    val logger by LoggerDelegate()

    override fun handle(serverWebExchange: ServerWebExchange, ex: Throwable): Mono<Void> = mono {

        logger.error(ex.stackTraceToString())

        var statusCode = HttpStatus.INTERNAL_SERVER_ERROR
        var errorCode = statusCode.value()
        if (ex is V1Exception) {
            statusCode = HttpStatus.OK
            errorCode = ex.errorCode
        }
        serverWebExchange.response.statusCode = statusCode
        serverWebExchange.response.headers.contentType = MediaType.APPLICATION_JSON

        val bufferFactory = serverWebExchange.response.bufferFactory()
        val result = ErrorResponse(
            code = errorCode,
            description = ex.localizedMessage
        )
        val dataBuffer = bufferFactory.wrap(objectMapper.writeValueAsBytes(result))
        serverWebExchange.response.writeWith(dataBuffer.toMono()).awaitFirstOrNull()
    }
}