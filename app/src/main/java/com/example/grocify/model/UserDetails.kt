package com.example.grocify.model

data class UserDetails(
    val uid: String,
    val addresses: MutableList<Address> = mutableListOf(),
    val paymentMethods: MutableList<PaymentMethod> = mutableListOf(),
)

data class Address(
    val name: String = "",
    val address: String = "",
    val city: String = "",
    val civic: Int = 0,
    var selected: Boolean = false
)

data class PaymentMethod(
    val owner: String,
    val number: String,
    val expireDate: String,
    val cvc: Int,
    val selected: Boolean
)
