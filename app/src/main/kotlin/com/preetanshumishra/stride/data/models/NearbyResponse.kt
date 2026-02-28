package com.preetanshumishra.stride.data.models

data class NearbyData(
    val nearbyPlaces: List<Place>,
    val linkedErrands: List<Errand>,
    val radiusKm: Double
)
