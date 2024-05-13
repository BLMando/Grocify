package com.example.grocify.data

data class SignInUiState(
    val isSuccessful: Boolean = false,
    val emailError: String = "",
    val isEmailValid: Boolean = true,
    val passwordError: String = "",
    val isPasswordValid: Boolean = true,
    val signInError: String? = null,
    val userRole: String? = null
)

data class GoogleSignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null,
    val userRole: String? = null
)

data class GoogleSignInResult(
    val data: UserData?,
    val error: String?
)

data class UserData(
    val name: String?,
    val surname: String?,
    val email: String?,
    val profilePic: String?,
    val role: String
)
