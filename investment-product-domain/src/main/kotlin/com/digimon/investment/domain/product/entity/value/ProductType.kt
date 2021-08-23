package com.digimon.investment.domain.product.entity.value

enum class ProductType {
    CREDIT, PROPERTY;

    companion object {
        fun of(type: String) =
            runCatching { valueOf(type.trim().uppercase()) }
                .getOrThrow()
    }
}
