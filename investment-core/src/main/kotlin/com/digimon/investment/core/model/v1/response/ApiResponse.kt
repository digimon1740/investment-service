package com.digimon.investment.core.model.v1.response

data class SingleResponse<out T>(
    val code: Int = 200,
    val description: String? = "정상 처리 되었습니다.",
    val data: T? = null,
)

data class MultiResponse<out T>(
    val code: Int = 200,
    val description: String? = "정상 처리 되었습니다.",
    val data: List<T>,
)

data class ErrorResponse(
    val code: Int = 0,
    val description: String? = null,
)