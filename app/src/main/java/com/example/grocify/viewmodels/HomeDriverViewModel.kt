package com.example.grocify.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocify.data.HomeDriverUiState
import com.example.grocify.model.Order
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.CancellationException

class HomeDriverViewModel(application: Application,private val mOneTapClient: SignInClient):AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(HomeDriverUiState())
    val uiState:StateFlow<HomeDriverUiState> = _uiState.asStateFlow()

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    fun getOrders(){
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        viewModelScope.launch {
            db.collection("orders")
                .whereEqualTo("date", currentDate.format(formatter) )
                .get()
                .addOnSuccessListener { documents ->
                    val ordersList:MutableList<Order> = mutableListOf()
                    for (document in documents) {
                        document.toObject(Order::class.java).let { order ->
                            ordersList.add(order)
                        }
                    }
                    _uiState.update { currentState ->
                        currentState.copy(orders = ordersList.toList())
                    }
                }
        }
    }

    fun signOut(){
        viewModelScope.launch {
            try {
                mOneTapClient.signOut()
                auth.signOut()
            }catch(e: Exception){
                e.printStackTrace()
                if(e is CancellationException) throw  e
            }
        }
    }


}