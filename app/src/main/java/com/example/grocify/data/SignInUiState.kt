package com.example.grocify.data

data class SignInUiState(
    val isSuccessful: Boolean = false,
    val emailError: String = "",
    val isEmailValid: Boolean = true,
    val passwordError: String = "",
    val isPasswordValid: Boolean = true,
    val signInError: String = ""
)
