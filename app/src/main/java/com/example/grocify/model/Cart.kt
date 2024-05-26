package com.example.grocify.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Carts")
data class Cart(
    @PrimaryKey val userId:String,
    val totalPrice: String,
    val type: String
)
