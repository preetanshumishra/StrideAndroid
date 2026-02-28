package com.preetanshumishra.stride.di

import android.content.Context
import com.preetanshumishra.stride.data.local.TokenManager
import com.preetanshumishra.stride.data.network.ApiService
import com.preetanshumishra.stride.data.network.TokenAuthenticator
import com.preetanshumishra.stride.data.network.TokenInterceptor
import com.preetanshumishra.stride.services.NearbyService
import com.preetanshumishra.stride.services.PlaceService
import com.preetanshumishra.stride.utils.LocationHelper
import com.preetanshumishra.stride.utils.WoosmapManager
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
object AppModule {

    private const val BASE_URL = "https://strideapi-1048111785674.us-central1.run.app/"

    @Provides
    @Singleton
    fun provideTokenManager(context: Context): TokenManager {
        return TokenManager(context)
    }

    @Provides
    @Singleton
    fun provideTokenInterceptor(tokenManager: TokenManager): TokenInterceptor {
        return TokenInterceptor(tokenManager)
    }

    @Provides
    @Singleton
    fun provideTokenAuthenticator(tokenManager: TokenManager): TokenAuthenticator {
        return TokenAuthenticator(tokenManager)
    }

    @Provides
    @Singleton
    fun provideLocationHelper(context: Context): LocationHelper {
        return LocationHelper(context)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(tokenInterceptor: TokenInterceptor, tokenAuthenticator: TokenAuthenticator): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(tokenInterceptor)
            .authenticator(tokenAuthenticator)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideWoosmapManager(
        context: Context,
        placeService: PlaceService,
        nearbyService: NearbyService,
        locationHelper: LocationHelper
    ): WoosmapManager {
        return WoosmapManager(context, placeService, nearbyService, locationHelper)
    }
}
