package com.example.grocify.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Carts", primaryKeys = ["type"])
data class Cart(
    val type: String,
    val totalPrice: Double,
)
