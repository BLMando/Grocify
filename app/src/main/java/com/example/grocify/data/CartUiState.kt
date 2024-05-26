package com.example.grocify.data

data class CartUiState(
    val productsList: MutableList<Product> = mutableListOf<Product>(),
    val totalPrice: String? = "0",
)

data class Product(
    val id: String,
    val name: String,
    val priceKg: Any?,
    val price: Any?,
    val quantity: String,
    val image: String,
    var units: Any?,
)