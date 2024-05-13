package com.example.grocify.data

data class CategoryItemsUiState(
    val categoryName : String = "",
    val products : List<Product> = emptyList(),
    val isSuccessful: Boolean = true
)

data class Product(
    val name: String,
    val priceKg: Any?,
    val price: Any?,
    val quantity: String,
    val image:String
)
