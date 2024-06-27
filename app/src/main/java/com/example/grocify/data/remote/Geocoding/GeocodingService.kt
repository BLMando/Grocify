package com.example.grocify.data.remote.Geocoding

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Interface to the Geocoding API.
 * getUserLocation does an API call to get the location of a given address.
 * The others query parameters are used to configure the API call.
 * @return a Response object containing the GeocodingResponse object.
 */
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