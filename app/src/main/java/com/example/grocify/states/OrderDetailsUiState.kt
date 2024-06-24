package com.example.grocify.states

import com.example.grocify.model.Product

data class OrderDetailsUiState(
    val products: List<Product> = emptyList(),
    val isProductsMarked: List<Boolean> = emptyList(),
    val destination: String = ""
)

