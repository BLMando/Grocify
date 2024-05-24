package com.example.grocify.data

import com.example.grocify.model.Address
import com.example.grocify.model.PaymentMethod

data class CheckoutUiState(
    val currentAddress: Address? = null,
    val currentPaymentMethod: PaymentMethod? = null,
    val totalPrice: Double = 0.0,
    val result: String = ""
)
