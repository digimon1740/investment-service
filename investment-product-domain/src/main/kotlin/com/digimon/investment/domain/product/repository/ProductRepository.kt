package com.digimon.investment.domain.product.repository

import com.digimon.investment.domain.product.entity.Product
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.time.LocalDateTime

interface ProductRepository : CoroutineCrudRepository<Product, Long> {

    @Query(
        """
        SELECT * 
        FROM products 
        WHERE id = :id AND (started_at < NOW()) AND (finished_at > NOW())
    """
    )
    suspend fun findByIdAndStartedAtBeforeAndFinishedAtAfter(id: Long): Product?

    suspend fun findAllByStartedAtBeforeAndFinishedAtAfterOrderByStartedAt(
        startedAt: LocalDateTime,
        finishedAt: LocalDateTime
    ): Flow<Product>
}