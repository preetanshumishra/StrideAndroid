package com.preetanshumishra.stride.utils

import com.preetanshumishra.stride.data.models.AuthResponse
import com.preetanshumishra.stride.data.models.User

fun AuthResponse.toUser() = User(
    id = userId,
    email = email,
    firstName = firstName,
    lastName = lastName
)
