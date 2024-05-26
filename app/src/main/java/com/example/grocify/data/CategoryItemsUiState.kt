package com.example.grocify.data

import com.example.grocify.model.Product

data class CategoryItemsUiState(
    val categoryName: String = "",
    val products: MutableList<Product> = mutableListOf<Product>(),
    var totalPrice: String = "1.50",
    val isSuccessful: Boolean = true
)


