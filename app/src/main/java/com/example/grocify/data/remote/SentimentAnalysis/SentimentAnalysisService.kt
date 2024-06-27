package com.example.grocify.data.remote.SentimentAnalysis

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interface to make API calls for Sentiment Analysis, makes an API call to AWS.
 * @param reviewText The text to be analyzed.
 * @return A Response object containing the SentimentAnalysisResponse object.
 */
interface SentimentAnalysisService {
    @GET("review_data")
    suspend fun getSentimentAnalysis(
        @Query("reviewText") reviewText: String
    ): Response<SentimentAnalysisResponse>
}