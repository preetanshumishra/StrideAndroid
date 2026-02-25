package com.preetanshumishra.stride.di

import com.preetanshumishra.stride.data.local.TokenManager
import com.preetanshumishra.stride.data.network.ApiService
import com.preetanshumishra.stride.services.AuthService
import com.preetanshumishra.stride.services.PlaceService
import com.preetanshumishra.stride.services.ErrandService

class AppDependencies(appComponent: AppComponent) {
    val authService: AuthService = appComponent.authService
    val placeService: PlaceService = appComponent.placeService
    val errandService: ErrandService = appComponent.errandService
    val tokenManager: TokenManager = appComponent.tokenManager
    val apiService: ApiService = appComponent.apiService
}

private lateinit var _appDependencies: AppDependencies

val appDependencies: AppDependencies
    get() = _appDependencies

fun initializeAppDependencies(dependencies: AppDependencies) {
    _appDependencies = dependencies
}
