package com.example.grocify.data

import com.example.grocify.model.Product

data class CartUiState(
    val products : List<Product> = emptyList(),
    val isSuccessful: Boolean = true
)