package com.example.grocify.model

data class Review(
    val orderId: String = "",
    val userId: String = "",
    val rating: Float = 0F,
    val review: String = "",
    val date: String = ""
)
