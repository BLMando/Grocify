package com.example.grocify.model

data class Order(
    val orderId: String,
    val cartId : String,
    val status: String?
)
