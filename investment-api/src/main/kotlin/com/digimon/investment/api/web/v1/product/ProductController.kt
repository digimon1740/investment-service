package com.digimon.investment.api.web.v1.product

import com.digimon.investment.api.service.product.ProductService
import com.digimon.investment.api.service.user.UserInvestmentService
import com.digimon.investment.api.web.v1.product.response.ProductOnSaleResponse
import com.digimon.investment.api.web.v1.product.response.ProductOnSaleResponseList
import com.digimon.investment.core.common.LoggerDelegate
import com.digimon.investment.core.model.v1.response.MultiResponse
import com.digimon.investment.domain.product.entity.Product
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/products")
class ProductController(
    private val productService: ProductService,
    private val userInvestmentService: UserInvestmentService,
) {
    private val logger by LoggerDelegate()

    @GetMapping
    suspend fun getAll(): MultiResponse<ProductOnSaleResponse> {
        val products = productService.getAllOnSale()
        val userInvestmentMap = userInvestmentService.getAllByProductIds(products.mapNotNull(Product::id))

        return MultiResponse(data = ProductOnSaleResponseList.of(products, userInvestmentMap).data)
    }

}