package com.example.grocify.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.grocify.model.Cart

@Dao
interface CartDAO {
    @Insert
    suspend fun insertCart(vararg cart: Cart)

    @Query("SELECT * FROM Carts WHERE type = :type")
    suspend fun getCart(type: String): MutableList<Cart>

    @Query("SELECT totalPrice FROM Carts WHERE type = :type")
    suspend fun getTotalPrice(type: String): Double

    @Query("UPDATE Carts SET totalPrice = totalPrice + :value WHERE type = :type")
    fun addValueToTotalPrice(type: String, value: Double)

    @Query("DELETE FROM Carts WHERE  type = :type")
    fun deleteCart(type: String)
}