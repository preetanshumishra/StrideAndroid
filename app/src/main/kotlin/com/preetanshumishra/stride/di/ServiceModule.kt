package com.preetanshumishra.stride.di

import com.preetanshumishra.stride.data.local.TokenManager
import com.preetanshumishra.stride.data.network.ApiService
import com.preetanshumishra.stride.services.AuthService
import com.preetanshumishra.stride.services.CollectionService
import com.preetanshumishra.stride.services.ErrandService
import com.preetanshumishra.stride.services.NearbyService
import com.preetanshumishra.stride.services.PlaceService
import com.preetanshumishra.stride.services.RouteService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object ServiceModule {

    @Provides
    @Singleton
    fun provideAuthService(
        apiService: ApiService,
        tokenManager: TokenManager
    ): AuthService {
        return AuthService(apiService, tokenManager)
    }

    @Provides
    @Singleton
    fun providePlaceService(apiService: ApiService): PlaceService {
        return PlaceService(apiService)
    }

    @Provides
    @Singleton
    fun provideErrandService(apiService: ApiService): ErrandService {
        return ErrandService(apiService)
    }

    @Provides
    @Singleton
    fun provideRouteService(apiService: ApiService): RouteService {
        return RouteService(apiService)
    }

    @Provides
    @Singleton
    fun provideNearbyService(apiService: ApiService): NearbyService {
        return NearbyService(apiService)
    }

    @Provides
    @Singleton
    fun provideCollectionService(apiService: ApiService): CollectionService {
        return CollectionService(apiService)
    }
}
