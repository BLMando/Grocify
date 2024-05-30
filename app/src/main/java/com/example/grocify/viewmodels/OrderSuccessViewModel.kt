package com.example.grocify.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.grocify.storage.Storage
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class OrderSuccessViewModel(application: Application): AndroidViewModel(application) {

    private val productDao = Storage.getInstance(getApplication<Application>().applicationContext).productDao()
    private val cartDao = Storage.getInstance(getApplication<Application>().applicationContext).cartDao()

    private val auth = Firebase.auth

    fun deleteOrder(type: String){
        productDao.deleteProductsList(type, auth.currentUser?.uid.toString())
        cartDao.deleteCart(type, auth.currentUser?.uid.toString())
    }
}