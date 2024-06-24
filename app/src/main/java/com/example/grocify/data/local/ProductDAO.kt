package com.example.grocify.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.wear.compose.material.FractionalThreshold
import com.example.grocify.model.Product


@Dao
interface ProductDAO {
    @Insert
    fun insertProduct(vararg product: Product)

    @Query("SELECT * FROM Products WHERE type = :type AND userId = :userId")
    suspend fun getProducts(type: String, userId: String): MutableList<Product>

    @Query("SELECT * FROM Products WHERE id = :productId AND threshold = :threshold AND type = :type AND userId = :userId")
    fun getProductByIdAndThreshold(productId: String, threshold: Int, type: String, userId: String): MutableList<Product>

    @Query("SELECT * FROM Products WHERE threshold = :threshold AND type = :type AND userId = :userId")
    fun getProductByThreshold(threshold: Int, type: String, userId: String): MutableList<Product>

    @Query("UPDATE Products SET units = units + :value WHERE id = :productId AND threshold = :threshold AND type = :type AND userId = :userId")
    fun addValueToProductUnits(productId: String, threshold: Int, type: String, userId: String, value: Int)

    @Query("DELETE FROM Products WHERE id = :productId AND threshold = :threshold AND type = :type AND userId = :userId")
    suspend fun deleteByIdAndThreshold(productId: String, threshold: Int, type: String, userId: String)

    @Query("DELETE FROM Products WHERE type = :type AND userId = :userId")
    fun deleteProductsList(type: String, userId: String)
}