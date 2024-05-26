package com.example.grocify.model

data class Product(
    val id: String,
    val name: String,
    val priceKg: Any?,
    val price: Any?,
    val quantity: String,
    val image: String,
    var units: Any?,
)
