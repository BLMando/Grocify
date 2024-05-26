package com.example.grocify.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GeocodingService {
    @GET("geocode/{address}.json")
    suspend fun getUserLocation(
        @Path("address") address:String,
        @Query("key") apiKey: String,
        @Query("storeResult") storeResult: Boolean = false,
        @Query("limit") limit: Int = 1,
        @Query("countrySet") countrySet: String = "it",
        @Query("language") language: String = "it-IT",
    ): Response<GeocodingResponse>
}