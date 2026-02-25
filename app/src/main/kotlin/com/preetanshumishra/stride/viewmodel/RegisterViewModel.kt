package com.preetanshumishra.stride.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.preetanshumishra.stride.services.AuthService
import com.preetanshumishra.stride.utils.Resource
import com.preetanshumishra.stride.utils.toUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authService: AuthService
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _firstName = MutableStateFlow("")
    val firstName: StateFlow<String> = _firstName.asStateFlow()

    private val _lastName = MutableStateFlow("")
    val lastName: StateFlow<String> = _lastName.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun updateEmail(newEmail: String) { _email.value = newEmail }
    fun updatePassword(newPassword: String) { _password.value = newPassword }
    fun updateFirstName(newFirstName: String) { _firstName.value = newFirstName }
    fun updateLastName(newLastName: String) { _lastName.value = newLastName }

    fun register() {
        viewModelScope.launch {
            if (!validateInputs()) return@launch

            _isLoading.value = true
            _errorMessage.value = null

            when (val result = authService.register(
                email = _email.value,
                password = _password.value,
                firstName = _firstName.value,
                lastName = _lastName.value
            )) {
                is Resource.Success -> {
                    result.data?.let { authResponse ->
                        authService.setAuthenticatedUser(
                            user = authResponse.toUser(),
                            accessToken = authResponse.accessToken,
                            refreshToken = authResponse.refreshToken
                        )
                    }
                }
                is Resource.Error -> { _errorMessage.value = result.message }
                is Resource.Loading -> {}
            }

            _isLoading.value = false
        }
    }

    private fun validateInputs(): Boolean {
        return when {
            _firstName.value.trim().isEmpty() -> { _errorMessage.value = "First name is required"; false }
            _lastName.value.trim().isEmpty() -> { _errorMessage.value = "Last name is required"; false }
            _email.value.trim().isEmpty() -> { _errorMessage.value = "Email is required"; false }
            _password.value.isEmpty() -> { _errorMessage.value = "Password is required"; false }
            _password.value.length < 8 -> { _errorMessage.value = "Password must be at least 8 characters"; false }
            else -> true
        }
    }
}
