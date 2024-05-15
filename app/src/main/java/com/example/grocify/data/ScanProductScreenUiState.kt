package com.example.grocify.data

data class ScanProductScreenUiState(
    val currentProduct: String? = null,
    val barcodeNonLetto: Boolean = true,
    val lista: List<Product?>? = null,
)

data class Product(
    val name: String,
    val priceKg: Any?,
    val price: Any?,
    val quantity: String,
    val image: String,
    val units: Int
)