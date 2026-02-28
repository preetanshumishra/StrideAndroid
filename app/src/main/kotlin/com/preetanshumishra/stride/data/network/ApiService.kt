package com.preetanshumishra.stride.data.network

import com.preetanshumishra.stride.data.models.ApiResponse
import com.preetanshumishra.stride.data.models.AuthResponse
import com.preetanshumishra.stride.data.models.ChangePasswordRequest
import com.preetanshumishra.stride.data.models.Errand
import com.preetanshumishra.stride.data.models.ErrandRequest
import com.preetanshumishra.stride.data.models.LocationRequest
import com.preetanshumishra.stride.data.models.LoginRequest
import com.preetanshumishra.stride.data.models.NearbyData
import com.preetanshumishra.stride.data.models.Place
import com.preetanshumishra.stride.data.models.PlaceCollection
import com.preetanshumishra.stride.data.models.PlaceRequest
import com.preetanshumishra.stride.data.models.RegisterRequest
import com.preetanshumishra.stride.data.models.UpdateProfileRequest
import com.preetanshumishra.stride.data.models.User
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @POST("/api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<AuthResponse>

    @POST("/api/v1/auth/register")
    suspend fun register(@Body request: RegisterRequest): ApiResponse<AuthResponse>

    @GET("/api/v1/auth/profile")
    suspend fun getProfile(): ApiResponse<User>

    @POST("/api/v1/auth/refresh")
    suspend fun refreshToken(@Body refreshToken: Map<String, String>): ApiResponse<Map<String, String>>

    @POST("/api/v1/auth/logout")
    suspend fun logout(@Body body: Map<String, String>): ApiResponse<Unit?>

    @GET("/api/v1/places")
    suspend fun getPlaces(): ApiResponse<List<Place>>

    @POST("/api/v1/places")
    suspend fun createPlace(@Body place: PlaceRequest): ApiResponse<Place>

    @GET("/api/v1/places/{id}")
    suspend fun getPlace(@Path("id") id: String): ApiResponse<Place>

    @PUT("/api/v1/places/{id}")
    suspend fun updatePlace(@Path("id") id: String, @Body place: PlaceRequest): ApiResponse<Place>

    @DELETE("/api/v1/places/{id}")
    suspend fun deletePlace(@Path("id") id: String): ApiResponse<Map<String, String>>

    @GET("/api/v1/errands")
    suspend fun getErrands(): ApiResponse<List<Errand>>

    @POST("/api/v1/errands")
    suspend fun createErrand(@Body errand: ErrandRequest): ApiResponse<Errand>

    @GET("/api/v1/errands/{id}")
    suspend fun getErrand(@Path("id") id: String): ApiResponse<Errand>

    @PUT("/api/v1/errands/{id}")
    suspend fun updateErrand(@Path("id") id: String, @Body errand: ErrandRequest): ApiResponse<Errand>

    @PATCH("/api/v1/errands/{id}/complete")
    suspend fun completeErrand(@Path("id") id: String): ApiResponse<Errand>

    @DELETE("/api/v1/errands/{id}")
    suspend fun deleteErrand(@Path("id") id: String): ApiResponse<Map<String, String>>

    @GET("/api/v1/collections")
    suspend fun getCollections(): ApiResponse<List<PlaceCollection>>

    @POST("/api/v1/collections")
    suspend fun createCollection(@Body collection: Map<String, Any>): ApiResponse<PlaceCollection>

    @GET("/api/v1/collections/{id}")
    suspend fun getCollection(@Path("id") id: String): ApiResponse<PlaceCollection>

    @PUT("/api/v1/collections/{id}")
    suspend fun updateCollection(@Path("id") id: String, @Body collection: Map<String, Any>): ApiResponse<PlaceCollection>

    @DELETE("/api/v1/collections/{id}")
    suspend fun deleteCollection(@Path("id") id: String): ApiResponse<Map<String, String>>

    @POST("/api/v1/errands/route")
    suspend fun getErrandRoute(@Body request: LocationRequest): ApiResponse<List<Errand>>

    @POST("/api/v1/nearby")
    suspend fun getNearby(@Body request: LocationRequest): ApiResponse<NearbyData>

    @PATCH("/api/v1/places/{id}/visit")
    suspend fun recordVisit(@Path("id") id: String): ApiResponse<Place>

    @PUT("/api/v1/auth/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): ApiResponse<User>

    @POST("/api/v1/auth/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): ApiResponse<Unit?>

    @DELETE("/api/v1/auth/account")
    suspend fun deleteAccount(): ApiResponse<Unit?>
}
