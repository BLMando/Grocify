package com.example.grocify.states

import com.example.grocify.model.Product

data class CartUiState(
    val productsList: MutableList<Product> = mutableListOf<Product>(),
    val totalPrice: Double = 0.00,
    val orderId: String = ""
)
