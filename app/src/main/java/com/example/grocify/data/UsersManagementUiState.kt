package com.example.grocify.data

import com.example.grocify.model.User

data class UsersManagementUiState(
    val users: MutableList<User> = mutableListOf<User>(),
    )