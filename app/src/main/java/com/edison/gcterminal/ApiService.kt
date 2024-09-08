package com.edison.gcterminal

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

// Define the API service interface
interface ApiService {
    @POST("validate")
    fun validateUser(@Body userCredentials: UserCredentials): Call<UserResponse>

    companion object {
        // Replace with your server's base URL
        private const val BASE_URL = "http://localhost:3000/"

        fun create(): ApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}

// Data class for the request body
data class UserCredentials(val username: String, val password: String)

// Data class for the response
data class UserResponse(val isValid: Boolean)
