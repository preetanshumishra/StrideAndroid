package com.preetanshumishra.stride.data.models

data class PlaceRequest(
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val category: String? = null,
    val notes: String? = null,
    val personalRating: Int? = null,
    val source: String = "manual",
    val tags: List<String>? = null,
    val collectionId: String? = null
)
