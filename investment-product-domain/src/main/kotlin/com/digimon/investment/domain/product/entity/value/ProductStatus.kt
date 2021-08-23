package com.digimon.investment.domain.product.entity.value

enum class ProductStatus {
    ON_SALE, SOLD_OUT;

    companion object {
        fun of(type: String) =
            runCatching { valueOf(type.trim().uppercase()) }
                .getOrThrow()
    }

}