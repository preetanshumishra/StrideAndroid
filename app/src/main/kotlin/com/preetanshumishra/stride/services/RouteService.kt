package com.preetanshumishra.stride.services

import com.preetanshumishra.stride.data.models.Errand
import com.preetanshumishra.stride.data.models.LocationRequest
import com.preetanshumishra.stride.data.network.ApiService
import com.preetanshumishra.stride.utils.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RouteService @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getRoute(latitude: Double, longitude: Double): Resource<List<Errand>> {
        return try {
            val response = apiService.getErrandRoute(LocationRequest(latitude, longitude))
            if (response.status == "success" && response.data != null) {
                Resource.Success(response.data)
            } else {
                Resource.Error(response.message ?: "Failed to get route")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Network error")
        }
    }
}
