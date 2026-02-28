package com.preetanshumishra.stride.data.models

data class RecurringRequest(
    val interval: Int,
    val unit: String
)

data class ErrandRequest(
    val title: String,
    val category: String? = null,
    val priority: String = "medium",
    val deadline: String? = null,
    val linkedPlaceId: String? = null,
    val recurring: RecurringRequest? = null
)
