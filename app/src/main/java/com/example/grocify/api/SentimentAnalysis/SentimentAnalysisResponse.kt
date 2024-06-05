package com.example.grocify.api.SentimentAnalysis

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SentimentAnalysisResponse(
    val chart_data: List<SentimentData>,
    val rating: Int
)

data class SentimentData(
    val entity:String,
    val positive:Double,
    val negative:Double,
    val mixed:Double,
    val neutral:Double
)




