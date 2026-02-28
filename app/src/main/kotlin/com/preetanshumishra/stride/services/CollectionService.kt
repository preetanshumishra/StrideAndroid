package com.preetanshumishra.stride.services

import com.preetanshumishra.stride.data.models.PlaceCollection
import com.preetanshumishra.stride.data.network.ApiService
import com.preetanshumishra.stride.utils.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CollectionService @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getCollections(): Resource<List<PlaceCollection>> {
        return try {
            val response = apiService.getCollections()
            if (response.status == "success") Resource.Success(response.data ?: emptyList())
            else Resource.Error(response.message ?: "Failed to load collections")
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Network error")
        }
    }

    suspend fun createCollection(name: String): Resource<PlaceCollection> {
        return try {
            val body = mapOf<String, Any>("name" to name)
            val response = apiService.createCollection(body)
            if (response.status == "success" && response.data != null) Resource.Success(response.data)
            else Resource.Error(response.message ?: "Failed to create collection")
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Network error")
        }
    }

    suspend fun updateCollection(id: String, name: String): Resource<PlaceCollection> {
        return try {
            val body = mapOf<String, Any>("name" to name)
            val response = apiService.updateCollection(id, body)
            if (response.status == "success" && response.data != null) Resource.Success(response.data)
            else Resource.Error(response.message ?: "Failed to update collection")
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Network error")
        }
    }

    suspend fun deleteCollection(id: String): Resource<Unit> {
        return try {
            apiService.deleteCollection(id)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Network error")
        }
    }
}
