package com.example.grocify.data

data class HomeUserUiState(
    val currentUserName: String? = null,
    val categories: List<String> = emptyList()
)
