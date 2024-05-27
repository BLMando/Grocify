package com.example.grocify.model

import androidx.room.Entity

@Entity(tableName = "Products", primaryKeys = ["id", "type", "userId"])
data class Product(
    val id: String = "",
    val type: String = "",
    val userId: String = "",
    val name: String = "",
    val priceKg:Double= 0.0,
    val price: Double = 0.0,
    val quantity: String = "",
    val image: String = "",
    var units: Int = 0
)

data class ProductType(
    val id: String,
    val name: String,
    val priceKg: Double,
    val price: Double,
    val quantity: String,
    val image: String,
)
