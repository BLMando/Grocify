package com.example.grocify.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Carts", primaryKeys = ["type", "userId"])
data class Cart(
    val type: String,
    val userId: String,
    val totalPrice: Double,
)
