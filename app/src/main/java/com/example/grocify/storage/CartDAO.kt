package com.example.grocify.storage

import androidx.room.Dao
import androidx.room.Insert
import com.example.grocify.model.Cart


@Dao
interface CartDAO {
    @Insert
    fun insertCart(vararg cart: Cart)
}