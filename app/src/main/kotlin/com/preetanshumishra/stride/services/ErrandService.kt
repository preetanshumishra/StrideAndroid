package com.preetanshumishra.stride.services

import com.preetanshumishra.stride.data.models.Errand
import com.preetanshumishra.stride.data.network.ApiService
import com.preetanshumishra.stride.utils.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ErrandService @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getErrands(): Resource<List<Errand>> {
        return try {
            val response = apiService.getErrands()
            if (response.status == "success" && response.data != null) {
                Resource.Success(response.data)
            } else {
                Resource.Error(response.message ?: "Failed to fetch errands")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Network error")
        }
    }

    suspend fun completeErrand(id: String): Resource<Errand> {
        return try {
            val response = apiService.completeErrand(id)
            if (response.status == "success" && response.data != null) {
                Resource.Success(response.data)
            } else {
                Resource.Error(response.message ?: "Failed to complete errand")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Network error")
        }
    }

    suspend fun deleteErrand(id: String): Resource<Unit> {
        return try {
            val response = apiService.deleteErrand(id)
            if (response.status == "success") {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message ?: "Failed to delete errand")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Network error")
        }
    }
}
