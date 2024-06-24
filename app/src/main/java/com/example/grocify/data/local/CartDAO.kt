package com.example.grocify.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.grocify.model.Cart

@Dao
interface CartDAO {
    @Insert
    suspend fun insertCart(vararg cart: Cart)

    @Query("SELECT * FROM Carts WHERE  type = :type AND userId = :userId")
    suspend fun getCart(type: String, userId: String): MutableList<Cart>

    @Query("SELECT totalPrice FROM Carts WHERE  type = :type AND userId = :userId")
    suspend fun getTotalPrice(type: String, userId: String): Double

    @Query("UPDATE Carts SET totalPrice = totalPrice + :value WHERE  type = :type AND userId = :userId")
    fun addValueToTotalPrice(type: String, userId: String, value: Double)

    @Query("DELETE FROM Carts WHERE type = :type AND userId = :userId")
    fun deleteCart(type: String, userId: String)
}