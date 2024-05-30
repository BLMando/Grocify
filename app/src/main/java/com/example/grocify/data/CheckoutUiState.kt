package com.example.grocify.data

import com.example.grocify.model.Address
import com.example.grocify.model.PaymentMethod

data class CheckoutUiState(
    val currentAddress: Address = Address(),
    val currentPaymentMethod: PaymentMethod? = null,
    val userHasRunningOrder: Boolean? = null,
    val orderId: String = "",
    val result: String = "",
    val resultAddress: String = "",
    val resultPaymentMethod: String = ""
)
