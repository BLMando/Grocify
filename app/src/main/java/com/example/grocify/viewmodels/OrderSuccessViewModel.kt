package com.example.grocify.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.grocify.data.local.Storage
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/**
 * ViewModel class for OrderSuccessScreen.
 * @param application The application context.
 */
class OrderSuccessViewModel(application: Application): AndroidViewModel(application) {

    private val productDao = Storage.getInstance(getApplication<Application>().applicationContext).productDao()
    private val cartDao = Storage.getInstance(getApplication<Application>().applicationContext).cartDao()

    private val auth = Firebase.auth

    /**
     * Deletes the products and cart of the user from local
     * storage after the order is placed.
     * @param type The type of the order: online or in-store.
     */
    fun deleteOrder(type: String){
        productDao.deleteProductsList(type, auth.currentUser?.uid.toString())
        cartDao.deleteCart(type, auth.currentUser?.uid.toString())
    }
}