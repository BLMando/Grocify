package com.example.grocify.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.grocify.model.Cart
import com.example.grocify.model.Product

/**
 * Defining the local database to store the cart items for the user.
 * Model class Cart and Product represent the tables in the database.
 */
@Database(entities = [Cart::class, Product::class], version = 1)
abstract class Storage: RoomDatabase() {

    /**
     * Methods to access the interfaces DAO for the queries
     */
    abstract fun cartDao(): CartDAO
    abstract fun productDao(): ProductDAO

    /**
     * Companion object to create a singleton instance of the database.
     * Volatile to ensure visibility of INSTANCE to all threads.
     * @param context Context of the application
     * @return Storage instance
     */
    companion object{
        @Volatile
        private var INSTANCE: Storage? = null
        fun getInstance(context: Context): Storage {
            return INSTANCE ?: synchronized(this){
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    Storage::class.java, "storage"
                ).allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE =it }
            }

        }
    }
}