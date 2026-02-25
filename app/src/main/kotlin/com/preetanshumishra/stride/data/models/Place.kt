package com.preetanshumishra.stride.data.models

import com.google.gson.annotations.SerializedName

data class Place(
    @SerializedName("_id") val id: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val category: String? = null,
    val tags: List<String> = emptyList(),
    val notes: String? = null,
    val personalRating: Int? = null,
    val collectionId: String? = null,
    val visitCount: Int = 0,
    val lastVisited: String? = null,
    val source: String = "manual"
)
