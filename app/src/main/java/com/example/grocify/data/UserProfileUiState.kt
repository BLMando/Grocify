package com.example.grocify.data

import androidx.core.content.ContextCompat.getString
import com.example.grocify.R
import com.example.grocify.model.User



data class UserProfileUiState(
    val user: User = User("", "", "", "", "","" ,"role"),
)
