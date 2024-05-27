package com.example.grocify.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.grocify.model.Product


@Dao
interface ProductDAO {
    @Insert
    fun insertProduct(vararg product: Product)

    @Query("SELECT * FROM Products WHERE type = :type AND userId = :userId")
    suspend fun getProducts(type: String, userId: String): MutableList<Product>

    @Query("SELECT * FROM Products WHERE id = :productId AND type = :type AND userId = :userId")
    fun getProductById(productId: String, type: String, userId: String): MutableList<Product>

    @Query("UPDATE Products SET units = units + :value WHERE id = :productId AND type = :type AND userId = :userId")
    fun addValueToProductUnits(productId: String, type: String, userId: String, value: Int)

    @Query("DELETE FROM Products WHERE id = :productId  AND type = :type AND userId = :userId")
    suspend fun deleteById(productId: String, type: String, userId: String)
}