package com.example.grocify.data

import com.example.grocify.model.Category
import com.example.grocify.model.Order


data class HomeUserUiState(
    val currentUserName: String? = "",
    val categories: List<Category> = emptyList()
)


data class HomeDriverUiState(
    val orders: List<Order> = emptyList()
)


