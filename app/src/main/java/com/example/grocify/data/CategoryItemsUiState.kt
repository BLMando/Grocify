package com.example.grocify.data

import com.example.grocify.model.Product

data class CategoryItemsUiState(
    val categoryName : String = "",
    val products : List<Product> = emptyList(),
    val isSuccessful: Boolean = true
)


