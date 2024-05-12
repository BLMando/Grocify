package com.example.grocify.data

data class HomeUserUiState(
    val currentUserName: String? = null,
    val categories: List<Category> = emptyList()
)

data class Category(
    val name: String = "" ,
    val image: String = ""
)
