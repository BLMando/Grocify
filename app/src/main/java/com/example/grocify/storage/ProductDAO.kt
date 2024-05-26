package com.example.grocify.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.grocify.model.Product


@Dao
interface ProductDAO {
    @Insert
    fun insertProduct(vararg product: Product)

    @Query("SELECT * FROM Products WHERE type = :type")
    suspend fun getProducts(type: String): MutableList<Product>

    @Query("SELECT * FROM Products WHERE id = :productId AND type = :type")
    fun getProductById(productId: String, type: String): MutableList<Product>

    @Query("UPDATE Products SET units = units + :value WHERE id = :productId AND type = :type")
    fun addValueToProductUnits(value: Int, productId: String, type: String)

    @Query("DELETE FROM Products WHERE id = :productId  AND type = :type")
    suspend fun deleteById(productId: String, type: String)
}