package com.preetanshumishra.stride.data.models

import com.google.gson.annotations.SerializedName

data class RecurringConfig(
    val enabled: Boolean = false,
    val intervalDays: Int? = null,
    val nextDue: String? = null
)

data class Errand(
    @SerializedName("_id") val id: String,
    val title: String,
    val category: String? = null,
    val linkedPlaceId: String? = null,
    val priority: String = "medium",
    val deadline: String? = null,
    val recurring: RecurringConfig? = null,
    val status: String = "pending",
    val completedAt: String? = null,
    val completedAtPlaceId: String? = null
)
