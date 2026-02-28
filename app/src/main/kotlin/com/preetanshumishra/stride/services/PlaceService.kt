package com.preetanshumishra.stride.services

import com.preetanshumishra.stride.data.models.Place
import com.preetanshumishra.stride.data.models.PlaceRequest
import com.preetanshumishra.stride.data.network.ApiService
import com.preetanshumishra.stride.utils.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaceService @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getPlaces(): Resource<List<Place>> {
        return try {
            val response = apiService.getPlaces()
            if (response.status == "success" && response.data != null) {
                Resource.Success(response.data)
            } else {
                Resource.Error(response.message ?: "Failed to fetch places")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Network error")
        }
    }

    suspend fun createPlace(request: PlaceRequest): Resource<Place> {
        return try {
            val response = apiService.createPlace(request)
            if (response.status == "success" && response.data != null) {
                Resource.Success(response.data)
            } else {
                Resource.Error(response.message ?: "Failed to create place")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Network error")
        }
    }

    suspend fun updatePlace(id: String, request: PlaceRequest): Resource<Place> {
        return try {
            val response = apiService.updatePlace(id, request)
            if (response.status == "success" && response.data != null) {
                Resource.Success(response.data)
            } else {
                Resource.Error(response.message ?: "Failed to update place")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Network error")
        }
    }

    suspend fun deletePlace(id: String): Resource<Unit> {
        return try {
            val response = apiService.deletePlace(id)
            if (response.status == "success") {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message ?: "Failed to delete place")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Network error")
        }
    }

    suspend fun recordVisit(id: String): Resource<Place> {
        return try {
            val response = apiService.recordVisit(id)
            if (response.status == "success" && response.data != null) {
                Resource.Success(response.data)
            } else {
                Resource.Error(response.message ?: "Failed to record visit")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Network error")
        }
    }
}
