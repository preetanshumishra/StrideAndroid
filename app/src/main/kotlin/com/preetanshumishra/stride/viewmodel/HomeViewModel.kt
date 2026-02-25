package com.preetanshumishra.stride.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.preetanshumishra.stride.data.models.User
import com.preetanshumishra.stride.services.AuthService
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val authService: AuthService
) : ViewModel() {

    val user: StateFlow<User?> = authService.user

    fun logout() {
        viewModelScope.launch {
            authService.logout()
        }
    }
}
