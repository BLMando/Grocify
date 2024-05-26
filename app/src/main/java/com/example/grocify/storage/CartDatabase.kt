package com.example.grocify.storage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.grocify.model.Cart


@Database(entities = [Cart::class], version = 1)
abstract class CartDatabase: RoomDatabase() {

    abstract fun cartDao(): CartDAO

    companion object{
        @Volatile
        private var INSTANCE: CartDatabase? = null
        fun getInstance(context: Context): CartDatabase{
            return INSTANCE ?: synchronized(this){
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    CartDatabase::class.java, "cart_database"
                ).allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE =it }
            }

        }
    }
}