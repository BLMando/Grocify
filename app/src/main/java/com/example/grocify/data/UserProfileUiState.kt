package com.example.grocify.data

import com.example.grocify.model.User



data class UserProfileUiState(
    val user: User = User("", "", "", "", "","" ,"role"),
)
