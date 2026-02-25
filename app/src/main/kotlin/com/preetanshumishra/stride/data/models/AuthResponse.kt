package com.preetanshumishra.stride.data.models

data class AuthResponse(
    val userId: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val accessToken: String,
    val refreshToken: String
)
