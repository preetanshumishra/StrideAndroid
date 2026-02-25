package com.preetanshumishra.stride.data.models

import com.google.gson.annotations.SerializedName

data class PlaceCollection(
    @SerializedName("_id") val id: String,
    val name: String,
    val icon: String = "📁",
    val shared: Boolean = false
)
