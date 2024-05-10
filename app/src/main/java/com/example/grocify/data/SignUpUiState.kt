package com.example.grocify.data

data class SignUpUiState(
    val isSuccessful: Boolean = false,
    val emailError: String = "",
    val isEmailValid: Boolean = true,
    val passwordError: String = "",
    val isPasswordValid: Boolean = true,
    val nameError: String = "",
    val isNameValid: Boolean = true,
    val surnameError: String = "",
    val isSurnameValid: Boolean = true,
    val signUpError: String = ""
)
