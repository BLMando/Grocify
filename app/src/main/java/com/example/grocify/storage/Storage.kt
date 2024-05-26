package com.example.grocify.storage

import android.content.Context
import androidx.room.Room
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.grocify.model.Cart
import com.example.grocify.model.Product


@Database(entities = [Cart::class, Product::class], version = 1)
abstract class Storage: RoomDatabase() {

    abstract fun cartDao(): CartDAO
    abstract fun productDao(): ProductDAO

    companion object{
        @Volatile
        private var INSTANCE: Storage? = null
        fun getInstance(context: Context): Storage{
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