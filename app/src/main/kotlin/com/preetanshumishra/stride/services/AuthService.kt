package com.preetanshumishra.stride.services

import com.preetanshumishra.stride.data.local.TokenManager
import com.preetanshumishra.stride.data.models.AuthResponse
import com.preetanshumishra.stride.data.models.ChangePasswordRequest
import com.preetanshumishra.stride.data.models.UpdateProfileRequest
import com.preetanshumishra.stride.data.models.User
import com.preetanshumishra.stride.data.network.ApiService
import com.preetanshumishra.stride.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthService @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) {
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    init {
        serviceScope.launch {
            tokenManager.tokenRefreshFailed.collect {
                clearAuthentication()
            }
        }
    }

    suspend fun login(email: String, password: String): Resource<AuthResponse> {
        return try {
            val response = apiService.login(
                com.preetanshumishra.stride.data.models.LoginRequest(email, password)
            )

            if (response.status == "success" && response.data != null) {
                Resource.Success(response.data)
            } else {
                Resource.Error(response.message ?: "Login failed")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Network error")
        }
    }

    suspend fun register(
        email: String,
        password: String,
        firstName: String,
        lastName: String
    ): Resource<AuthResponse> {
        return try {
            val request = com.preetanshumishra.stride.data.models.RegisterRequest(
                email, password, firstName, lastName
            )
            val response = apiService.register(request)

            if (response.status == "success" && response.data != null) {
                Resource.Success(response.data)
            } else {
                Resource.Error(response.message ?: "Registration failed")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Network error")
        }
    }

    suspend fun getProfile(): Resource<User> {
        return try {
            val response = apiService.getProfile()

            if (response.status == "success" && response.data != null) {
                Resource.Success(response.data)
            } else {
                Resource.Error(response.message ?: "Failed to fetch profile")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Network error")
        }
    }

    suspend fun setAuthenticatedUser(user: User, accessToken: String, refreshToken: String) {
        tokenManager.saveTokens(accessToken, refreshToken)
        _user.value = user
        _isLoggedIn.value = true
    }

    suspend fun logout() {
        val refreshToken = tokenManager.getRefreshTokenSync()
        if (refreshToken != null) {
            try { apiService.logout(mapOf("refreshToken" to refreshToken)) } catch (_: Exception) {}
        }
        clearAuthentication()
    }

    suspend fun clearAuthentication() {
        tokenManager.clearTokens()
        _user.value = null
        _isLoggedIn.value = false
    }

    suspend fun checkPersistedAuth() {
        val token = tokenManager.getAccessTokenSync()
        _isLoggedIn.value = token != null

        if (token != null) {
            when (val result = getProfile()) {
                is Resource.Success -> _user.value = result.data
                else -> {
                    clearAuthentication()
                }
            }
        }
    }

    suspend fun updateProfile(firstName: String, lastName: String, email: String): Resource<User> {
        return try {
            val response = apiService.updateProfile(UpdateProfileRequest(firstName, lastName, email))
            if (response.status == "success" && response.data != null) {
                _user.value = response.data
                Resource.Success(response.data)
            } else {
                Resource.Error(response.message ?: "Failed to update profile")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Network error")
        }
    }

    suspend fun changePassword(currentPassword: String, newPassword: String): Resource<Unit> {
        return try {
            val response = apiService.changePassword(ChangePasswordRequest(currentPassword, newPassword))
            if (response.status == "success") {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message ?: "Failed to change password")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Network error")
        }
    }

    suspend fun deleteAccount(): Resource<Unit> {
        return try {
            val response = apiService.deleteAccount()
            if (response.status == "success") {
                clearAuthentication()
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message ?: "Failed to delete account")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Network error")
        }
    }
}
