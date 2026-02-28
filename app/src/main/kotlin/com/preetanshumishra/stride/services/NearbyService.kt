package com.preetanshumishra.stride.services

import com.preetanshumishra.stride.data.models.LocationRequest
import com.preetanshumishra.stride.data.models.NearbyData
import com.preetanshumishra.stride.data.network.ApiService
import com.preetanshumishra.stride.utils.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NearbyService @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getNearby(latitude: Double, longitude: Double, radiusKm: Double = 1.0): Resource<NearbyData> {
        return try {
            val response = apiService.getNearby(LocationRequest(latitude, longitude, radiusKm))
            if (response.status == "success" && response.data != null) {
                Resource.Success(response.data)
            } else {
                Resource.Error(response.message ?: "Failed to get nearby")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Network error")
        }
    }
}
