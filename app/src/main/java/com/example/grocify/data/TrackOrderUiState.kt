package com.example.grocify.data

import com.example.grocify.model.Order

data class TrackOrderUiState(
    val order: Order = Order(),
    val name: String = "",
    val surname:  String = "",
)
