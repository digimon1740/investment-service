package com.digimon.investment.core.cache

import com.digimon.investment.core.common.LoggerDelegate
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.data.redis.core.deleteAndAwait
import org.springframework.data.redis.core.getAndAwait
import org.springframework.data.redis.core.setAndAwait
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.time.Duration
import java.time.Duration.ofMillis


@Component
class InvestServiceCacheManager(
    @Qualifier("investServiceReactiveRedisTemplate")
    private val redisOperations: ReactiveRedisOperations<String, Any>,
) {

    enum class CacheTimePair(val ttl: Duration) {
        `1_MIN`(ofMillis(60L)),
        `5_MIN`(ofMillis(300L)),
        `10_MIN`(ofMillis(600L)),
        `30_MIN`(ofMillis(1800)),
        `1_HOUR`(ofMillis(3600L)),
        `6_HOURS`(ofMillis(7200L)),
        `12_HOURS`(ofMillis(43200L)),
        `1_DAY`(ofMillis(86400L)),
        `2_DAYS`(ofMillis(172800L)),
        `1_WEEK`(ofMillis(604800L));
    }

    val logger by LoggerDelegate()

    suspend fun <T> awaitGetOrNull(key: String): T? {
        return redisOperations
            .opsForValue()
            .getAndAwait(key) as T?
    }

    suspend fun <T> awaitGetOrPut(key: String, ttl: Duration, block: suspend () -> T?): T? {
        return awaitGetOrNull<T>(key) ?: run {
            val result = block()
            redisOperations.opsForValue()
                .setAndAwait(key, result as Any, ttl)
            result
        }
    }

    suspend fun <T> awaitGetOrPut(key: String, block: suspend () -> T?): T? {
        return awaitGetOrNull<T>(key) ?: run {
            val result = block()
            awaitPut(key, result as Any)
            result
        }
    }

    suspend fun <T> awaitGetOrPutNotNull(key: String, block: suspend () -> T): T {
        return awaitGetOrPut(key, block)!!
    }

    suspend fun <T> awaitGetOrPutNotNull(key: String, ttl: Duration, block: suspend () -> T): T {
        return awaitGetOrPut(key, ttl, block)!!
    }

    suspend fun <T> awaitPut(key: String, ttl: Duration, block: suspend () -> T?): T? =
        block()?.let {
            redisOperations.opsForValue()
                .setAndAwait(key, it as Any, ttl)
            it
        }


    suspend fun <T> awaitPut(key: String, block: suspend () -> T?): T? =
        block()?.let {
            redisOperations.opsForValue()
                .setAndAwait(key, it as Any)
            it
        }

    suspend fun <T> awaitPut(key: String, value: T?): T? =
        value?.let {
            redisOperations.opsForValue()
                .setAndAwait(key, it)
            it
        }

    suspend fun awaitRemove(key: String) {
        redisOperations.opsForValue().deleteAndAwait(key)
    }

    fun <T> getOrNull(key: String): Mono<T> {
        return redisOperations
            .opsForValue()
            .get(key)
            .flatMap {
                Mono.justOrEmpty(it as T?)
            }
    }

    fun <T> getOrPut(key: String, ttl: Duration, block: () -> T?): Mono<T> {
        return redisOperations.opsForValue()
            .get(key)
            .flatMap {
                Mono.justOrEmpty(it as T?)
            }
            .switchIfEmpty {
                put(key, ttl, block)
            }
    }

    fun <T> getOrPut(key: String, block: () -> T?): Mono<T> {
        return redisOperations.opsForValue()
            .get(key)
            .flatMap {
                Mono.justOrEmpty(it as T?)
            }
            .switchIfEmpty {
                put(key, block)
            }
    }

    fun <T> put(key: String, block: () -> T?): Mono<T> {
        val result = block()
        return Mono.justOrEmpty(result).flatMap {
            redisOperations.opsForValue()
                .set(key, it as Any)
                .flatMap {
                    Mono.justOrEmpty(result)
                }
        }
    }

    fun <T> put(key: String, ttl: Duration, block: () -> T?): Mono<T> {
        val result = block()
        return Mono.justOrEmpty(result).flatMap {
            redisOperations.opsForValue()
                .set(key, it as Any, ttl)
                .flatMap {
                    Mono.justOrEmpty(result)
                }
        }
    }

    fun remove(key: String): Mono<Boolean> {
        return redisOperations.opsForValue().delete(key)
    }
}
