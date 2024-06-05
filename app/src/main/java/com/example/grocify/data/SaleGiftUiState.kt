package com.example.grocify.data

import com.example.grocify.model.ProductType

data class SaleGiftUiState(
    val products: MutableList<ProductType> = mutableListOf<ProductType>(),
    val selectedProducts: MutableList<ProductType> = mutableListOf<ProductType>(),
)