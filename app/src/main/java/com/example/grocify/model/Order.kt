package com.example.grocify.model

data class Order(
    val orderId: String = "",
    val cart: List<HashMap<String,Any>> = emptyList(),
    val userId: String = "",
    val status: String = "",
    val destination: String = "",
    val totalPrice: Double = 0.0,
    val type: String = "",
    val date: String = "",
    val time: String = "",
    val driverId: String = "",
)
