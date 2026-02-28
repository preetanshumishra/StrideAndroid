package com.preetanshumishra.stride.di

import android.content.Context
import com.preetanshumishra.stride.MainActivity
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
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, ServiceModule::class])
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun context(context: Context): Builder

        fun build(): AppComponent
    }

    fun inject(activity: MainActivity)

    val authService: AuthService
    val placeService: PlaceService
    val errandService: ErrandService
    val tokenManager: TokenManager
    val apiService: ApiService
    val routeService: RouteService
    val nearbyService: NearbyService
    val locationHelper: LocationHelper
    val collectionService: CollectionService
    val woosmapManager: WoosmapManager
}
