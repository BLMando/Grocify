package com.example.grocify.data

import com.example.grocify.model.Category
import com.example.grocify.model.Order
import com.example.grocify.model.Review


data class HomeUserUiState(
    val currentUserName: String? = "",
    val categories: List<Category> = emptyList(),
    val orderId: String = ""
)


data class HomeDriverUiState(
    val currentUserName: String? = "",
    val orders: List<Order> = emptyList()
)

data class HomeAdminUiState(
    val currentUserName: String? = "",
    val reviews: List<Review> = emptyList()
)


