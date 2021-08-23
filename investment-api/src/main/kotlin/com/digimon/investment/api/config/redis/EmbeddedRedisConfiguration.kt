package com.digimon.investment.api.config.redis

import com.digimon.investment.core.common.LoggerDelegate
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.util.StringUtils
import redis.embedded.RedisServer
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy


@Configuration
class EmbeddedRedisConfiguration(
    @Value("\${spring.redis.port}") val redisPort: Int
) {

    val logger by LoggerDelegate()

    lateinit var redisServer: RedisServer

    @PostConstruct
    fun startRedis() {
        var port = redisPort
        try {
            port = if (isRedisRunning()) findAvailablePort() else port
            redisServer = RedisServer(port)
            redisServer.start()
            logger.info("EmbeddedRedis started")
        } catch (e: Exception) {
            redisServer = RedisServer(port + 101)
            redisServer.start()
            logger.info("EmbeddedRedis started")
        }
    }

    @PreDestroy
    fun stopRedis() {
        redisServer.stop()
        logger.info("EmbeddedRedis stopped")
    }

    /**
     * Embedded Redis가 현재 실행중인지 확인
     */
    @Throws(IOException::class)
    fun isRedisRunning(): Boolean {
        return isRunning(executeGrepProcessCommand(redisPort))
    }


    @Throws(IOException::class)
    fun findAvailablePort(): Int {
        for (port in 10000..65535) {
            val process = executeGrepProcessCommand(port)
            if (!isRunning(process)) {
                return port
            }
        }
        throw IllegalArgumentException("Not Found Available port: 10000 ~ 65535")
    }


    @Throws(IOException::class)
    fun executeGrepProcessCommand(port: Int): Process {
        val command = String.format("netstat -nat | grep LISTEN|grep %d", port)
        val shell = arrayOf("/bin/sh", "-c", command)
        return Runtime.getRuntime().exec(shell)
    }

    fun isRunning(process: Process): Boolean {
        var line: String?
        val pidInfo = StringBuilder()
        try {
            BufferedReader(InputStreamReader(process.inputStream)).use { input ->
                while (input.readLine().also { line = it } != null) {
                    pidInfo.append(line)
                }
            }
        } catch (e: Exception) {
        }
        return !StringUtils.isEmpty(pidInfo.toString())
    }

}