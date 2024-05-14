package com.example.grocify.data

import com.example.grocify.model.Category


data class HomeUserUiState(
    val currentUserName: String? = null,
    val categories: List<Category> = emptyList()
)


