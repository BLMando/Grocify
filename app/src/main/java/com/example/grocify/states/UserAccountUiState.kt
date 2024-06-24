package com.example.grocify.states

import com.example.grocify.model.Address
import com.example.grocify.model.Order
import com.example.grocify.model.PaymentMethod
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
    val error: String = ""
)


data class UserAddressesUiState(
    val addresses: List<Address> = emptyList(),
    val addressToUpdate: Address? = null,
    val result: String = "",
    val isFABClicked: Boolean = false,
    val isInsertSuccessful: Boolean = false,
    val isUDSuccessful: Boolean = false,
    val addressNameError: String = "",
    val isAddressNameValid: Boolean = true,
    val cityError: String = "",
    val isCityValid: Boolean = true,
    val addressError: String = "",
    val isAddressValid: Boolean = true,
    val civicError: String = "",
    val isCivicValid: Boolean = true
)

data class UserOrdersUiState(
    val isReviewClicked: Boolean = false,
    val ordersReviewed: List<String> = emptyList(),
    val orders:  List<Order> = emptyList(),
    val orderReview: Order = Order(),
    val textError: String = "",
    val isTextValid: Boolean = true
)

data class UserPaymentMethodsUiState(
    val paymentMethods: List<PaymentMethod> = emptyList(),
    val paymentMethodToUpdate: PaymentMethod? = null,
    val result: String = "",
    val isFABClicked: Boolean = false,
    val isInsertSuccessful: Boolean = false,
    val isUDSuccessful: Boolean = false,
    val ownerError: String = "",
    val isOwnerValid: Boolean = true,
    val numberError: String = "",
    val isNumberValid: Boolean = true,
    val cvcError: String = "",
    val isCvcValid: Boolean = true,
    val expireDateError: String = "",
    val isExpireDateValid: Boolean = true
)