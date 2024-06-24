package com.example.grocify.states

import com.example.grocify.model.ProductType

data class CategoryItemsUiState(
    val categoryName: String = "",
    val products: MutableList<ProductType> = mutableListOf<ProductType>(),
    val isSuccessful: Boolean = true
)


