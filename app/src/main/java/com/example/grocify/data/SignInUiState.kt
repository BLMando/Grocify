package com.example.grocify.data

import com.example.grocify.model.User

data class SignInUiState(
    val isSuccessful: Boolean = false,
    val emailError: String = "",
    val isEmailValid: Boolean = true,
    val passwordError: String = "",
    val isPasswordValid: Boolean = true,
    val signInError: String? = null
)

data class GoogleSignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)

data class GoogleSignInResult(
    val data: User?,
    val error: String?
)
