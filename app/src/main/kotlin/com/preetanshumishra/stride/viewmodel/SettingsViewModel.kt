package com.preetanshumishra.stride.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.preetanshumishra.stride.services.AuthService
import com.preetanshumishra.stride.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val authService: AuthService
) : ViewModel() {

    var firstName = MutableStateFlow("")
    var lastName = MutableStateFlow("")
    var email = MutableStateFlow("")
    var currentPassword = MutableStateFlow("")
    var newPassword = MutableStateFlow("")
    var confirmPassword = MutableStateFlow("")

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _showDeleteConfirm = MutableStateFlow(false)
    val showDeleteConfirm: StateFlow<Boolean> = _showDeleteConfirm.asStateFlow()

    init {
        authService.user.value?.let { user ->
            firstName.value = user.firstName
            lastName.value = user.lastName
            email.value = user.email
        }
    }

    fun saveProfile() {
        val fn = firstName.value.trim()
        val ln = lastName.value.trim()
        val em = email.value.trim()
        if (fn.isEmpty() || ln.isEmpty() || em.isEmpty()) {
            _errorMessage.value = "All fields are required"
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            when (authService.updateProfile(fn, ln, em)) {
                is Resource.Success -> _successMessage.value = "Profile updated"
                is Resource.Error -> _errorMessage.value = "Failed to update profile"
                is Resource.Loading -> {}
            }
            _isLoading.value = false
        }
    }

    fun changePassword() {
        val current = currentPassword.value
        val new = newPassword.value
        val confirm = confirmPassword.value
        if (current.isEmpty() || new.isEmpty() || confirm.isEmpty()) {
            _errorMessage.value = "All password fields are required"
            return
        }
        if (new != confirm) {
            _errorMessage.value = "New passwords do not match"
            return
        }
        if (new.length < 8) {
            _errorMessage.value = "New password must be at least 8 characters"
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            when (authService.changePassword(current, new)) {
                is Resource.Success -> {
                    currentPassword.value = ""
                    newPassword.value = ""
                    confirmPassword.value = ""
                    _successMessage.value = "Password changed"
                }
                is Resource.Error -> _errorMessage.value = "Failed to change password"
                is Resource.Loading -> {}
            }
            _isLoading.value = false
        }
    }

    fun requestDeleteAccount() {
        _showDeleteConfirm.value = true
    }

    fun confirmDeleteAccount() {
        _showDeleteConfirm.value = false
        viewModelScope.launch {
            _isLoading.value = true
            authService.deleteAccount()
            _isLoading.value = false
        }
    }

    fun dismissDeleteConfirm() {
        _showDeleteConfirm.value = false
    }

    fun clearMessages() {
        _successMessage.value = null
        _errorMessage.value = null
    }
}
