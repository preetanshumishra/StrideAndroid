package com.preetanshumishra.stride.data.network

import com.google.gson.JsonParser
import com.preetanshumishra.stride.data.local.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val tokenManager: TokenManager
) : Authenticator {

    private val plainClient = OkHttpClient()
    private val baseUrl = "http://10.0.2.2:5001"

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.request.url.encodedPath.contains("/auth/refresh")) return null

        val newAccessToken = runBlocking {
            try {
                val refreshToken = tokenManager.getRefreshTokenSync() ?: run {
                    tokenManager.onRefreshFailed()
                    return@runBlocking null
                }

                val body = """{"refreshToken":"$refreshToken"}"""
                    .toRequestBody("application/json".toMediaType())

                val request = Request.Builder()
                    .url("$baseUrl/api/v1/auth/refresh")
                    .post(body)
                    .build()

                val refreshResponse = plainClient.newCall(request).execute()
                if (!refreshResponse.isSuccessful) {
                    tokenManager.onRefreshFailed()
                    return@runBlocking null
                }

                val responseBody = refreshResponse.body?.string() ?: run {
                    tokenManager.onRefreshFailed()
                    return@runBlocking null
                }

                val json = JsonParser.parseString(responseBody).asJsonObject
                if (json.get("status")?.asString != "success") {
                    tokenManager.onRefreshFailed()
                    return@runBlocking null
                }

                val data = json.getAsJsonObject("data") ?: run {
                    tokenManager.onRefreshFailed()
                    return@runBlocking null
                }
                val newToken = data.get("accessToken")?.asString ?: run {
                    tokenManager.onRefreshFailed()
                    return@runBlocking null
                }
                val newRefresh = data.get("refreshToken")?.asString ?: refreshToken

                tokenManager.saveTokens(newToken, newRefresh)
                newToken
            } catch (e: Exception) {
                tokenManager.onRefreshFailed()
                null
            }
        }

        return newAccessToken?.let {
            response.request.newBuilder()
                .header("Authorization", "Bearer $it")
                .build()
        }
    }
}
