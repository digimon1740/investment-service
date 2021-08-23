package com.digimon.investment.api.config.redis

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfiguration{

    @Bean
    fun investServiceReactiveRedisTemplate(
        connectionFactory: ReactiveRedisConnectionFactory,
        objectMapper: ObjectMapper,
    ): ReactiveRedisTemplate<String, Any> {
        val keySerializer = StringRedisSerializer()
        val valueSerializer = JdkSerializationRedisSerializer()
        val serializationContext =
            RedisSerializationContext.newSerializationContext<String, Any>()
                .key(keySerializer)
                .value(JdkSerializationRedisSerializer())
                .hashKey(keySerializer)
                .hashValue(valueSerializer)
                .build()
        return ReactiveRedisTemplate(connectionFactory, serializationContext)
    }
}