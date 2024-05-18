package com.example.grocify.data

import com.example.grocify.model.User

data class UserAccountUiState(
    val user: User = User(
        null,
        "",
        "",
        "",
        "",
        null ,
        "user"
    ),
)

data class UserProfileUiState(
    val user: User = User(null, "", "", "", "", null , "role"),
    val isSuccessful: Boolean = false,
    val passwordError: String = "",
    val isPasswordValid: Boolean = true,
    val nameError: String = "",
    val isNameValid: Boolean = true,
    val surnameError: String = "",
    val isSurnameValid: Boolean = true,
    val error: String? = null
)


data class UserAddressesUiState(
    val isFABClicked: Boolean = false
)

data class UserOrdersUiState(
    val isReviewClicked: Boolean = false
)

data class UserPaymentMethodsUiState(
    val isFABClicked: Boolean = false
)