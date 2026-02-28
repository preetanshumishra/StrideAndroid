package com.preetanshumishra.stride.di

import com.preetanshumishra.stride.data.local.TokenManager
import com.preetanshumishra.stride.data.network.ApiService
import com.preetanshumishra.stride.services.AuthService
import com.preetanshumishra.stride.services.CollectionService
import com.preetanshumishra.stride.services.ErrandService
import com.preetanshumishra.stride.services.NearbyService
import com.preetanshumishra.stride.services.PlaceService
import com.preetanshumishra.stride.services.RouteService
import com.preetanshumishra.stride.utils.LocationHelper
import com.preetanshumishra.stride.utils.WoosmapManager

class AppDependencies(appComponent: AppComponent) {
    val authService: AuthService = appComponent.authService
    val placeService: PlaceService = appComponent.placeService
    val errandService: ErrandService = appComponent.errandService
    val tokenManager: TokenManager = appComponent.tokenManager
    val apiService: ApiService = appComponent.apiService
    val routeService: RouteService = appComponent.routeService
    val nearbyService: NearbyService = appComponent.nearbyService
    val locationHelper: LocationHelper = appComponent.locationHelper
    val collectionService: CollectionService = appComponent.collectionService
    val woosmapManager: WoosmapManager by lazy { appComponent.woosmapManager }
}

private lateinit var _appDependencies: AppDependencies

val appDependencies: AppDependencies
    get() = _appDependencies

fun initializeAppDependencies(dependencies: AppDependencies) {
    _appDependencies = dependencies
}
