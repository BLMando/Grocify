package com.example.grocify.states

data class SignUpUiState(
    val isSuccessful: Boolean = false,
    val emailError: String = "",
    val isEmailValid: Boolean = true,
    val passwordError: String = "",
    val isPasswordValid: Boolean = true,
    val confirmPasswordError: String = "",
    val isConfirmPasswordValid: Boolean = true,
    val nameError: String = "",
    val isNameValid: Boolean = true,
    val surnameError: String = "",
    val isSurnameValid: Boolean = true,
    val signUpError: String? = null
)

