package com.preetanshumishra.stride.data.models

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)
