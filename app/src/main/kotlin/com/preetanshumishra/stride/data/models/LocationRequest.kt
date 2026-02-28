package com.preetanshumishra.stride.data.models

data class LocationRequest(
    val latitude: Double,
    val longitude: Double,
    val radiusKm: Double? = null
)
