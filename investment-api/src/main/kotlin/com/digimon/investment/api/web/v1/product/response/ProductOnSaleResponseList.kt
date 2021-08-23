package com.digimon.investment.api.web.v1.product.response

import com.digimon.investment.domain.product.entity.Product
import com.digimon.investment.domain.user.entity.UserInvestment

class ProductOnSaleResponseList(
    val data: List<ProductOnSaleResponse>,
    val size: Int = data.size
) {

    companion object {
        fun of(products: List<Product>, userInvestmentMap: Map<Long, List<UserInvestment>>): ProductOnSaleResponseList {
            val responses = products.map { product ->
                val userInvestments = userInvestmentMap[product.id]
                ProductOnSaleResponse.of(product, userInvestments)
            }
            return ProductOnSaleResponseList(responses)
        }
    }
}