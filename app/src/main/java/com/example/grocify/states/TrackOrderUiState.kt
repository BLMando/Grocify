package com.example.grocify.states

import com.example.grocify.model.Order

data class TrackOrderUiState(
    val order: Order = Order(),
    val name: String = "",
    val surname:  String = "",
)
