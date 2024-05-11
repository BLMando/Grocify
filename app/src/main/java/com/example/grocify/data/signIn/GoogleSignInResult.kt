package com.example.grocify.data.signIn

data class GoogleSignInResult(
    val data: UserData?,
    val error: String?
)

data class UserData(
    val name: String?,
    val surname: String?,
    val email: String?,
    val profilePic: String?
)


