package com.example.grocify.data

import com.example.grocify.model.Product

data class CartUiState(
    val productsList: MutableList<Product> = mutableListOf<Product>(),
    val totalPrice: String? = "0",
)
