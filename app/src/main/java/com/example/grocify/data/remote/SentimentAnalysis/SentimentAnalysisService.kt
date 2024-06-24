package com.example.grocify.data.remote.SentimentAnalysis

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SentimentAnalysisService {
    @GET("review_data")
    suspend fun getSentimentAnalysis(
        @Query("reviewText") reviewText: String
    ): Response<SentimentAnalysisResponse>
}