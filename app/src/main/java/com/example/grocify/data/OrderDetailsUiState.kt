package com.example.grocify.data

import com.example.grocify.model.Product

data class OrderDetailsUiState(
    val products: List<Product> = emptyList()
)

