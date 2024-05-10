package com.example.grocify.compose



data class GoogleSignInResult(
    val data: UserData?,
    val error: String?
)

data class UserData(
    val username: String?,
    val email: String?,
    val profilePic: String?
)


