package com.example.grocify.data.remote


import com.example.grocify.data.remote.Geocoding.GeocodingService
import com.example.grocify.data.remote.SentimentAnalysis.SentimentAnalysisService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


object RetrofitObject {
    private const val BASE_URL_TOMTOM = "https://api.tomtom.com/search/2/"
    private const val BASE_URL_AWS = " https://c5wk4pir8i.execute-api.eu-west-2.amazonaws.com/prod/"

    /**
     * Use the Moshi library to parse this JSON response.
     */
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    /**
     * The Retrofit object to make API call for Geocoding
     */
    val geocodingService: GeocodingService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_TOMTOM)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build().create(GeocodingService::class.java)
    }

    /**
     * The Retrofit object to make API call for Sentiment Analysis
     */
    val sentimentAnalysisService: SentimentAnalysisService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_AWS)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build().create(SentimentAnalysisService::class.java)
    }

}

