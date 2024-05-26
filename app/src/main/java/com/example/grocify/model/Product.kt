package com.example.grocify.model

import androidx.room.Entity

@Entity(tableName = "Products", primaryKeys = ["id", "type"])
data class Product(
    val id: String,
    val type: String,
    val name: String,
    val priceKg:Double,
    val price: Double,
    val quantity: String,
    val image: String,
    var units: Int,
)

data class ProductType(
    val id: String,
    val name: String,
    val priceKg: Double,
    val price: Double,
    val quantity: String,
    val image: String,
)
