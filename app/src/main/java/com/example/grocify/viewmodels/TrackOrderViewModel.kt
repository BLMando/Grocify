package com.example.grocify.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.grocify.data.TrackOrderUiState
import com.example.grocify.model.Order
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TrackOrderViewModel(application: Application): AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(TrackOrderUiState())
    val uiState:StateFlow<TrackOrderUiState> = _uiState.asStateFlow()

    private val db = Firebase.firestore

    fun getCurrentOrder(orderId: String){
        db.collection("orders")
            .whereEqualTo("orderId", orderId)
            .addSnapshotListener { documentSnapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }

                if (documentSnapshot!!.documents.isNotEmpty()) {
                    _uiState.update { currentState ->
                        documentSnapshot.documents[0].toObject(Order::class.java)?.let { order ->
                            currentState.copy(order = order)
                        } ?: currentState
                    }
                }
            }
    }

    fun getUserName(orderId: String){
        db.collection("orders")
            .whereEqualTo("orderId",orderId)
            .get()
            .addOnSuccessListener { document ->
                db.collection("users")
                    .whereEqualTo("uid",document.documents[0].data?.get("userId").toString())
                    .get()
                    .addOnSuccessListener { user ->
                        _uiState.update { currentState ->
                            currentState.copy(
                                name = user.documents[0].data?.get("name").toString().replaceFirstChar { it.uppercase() },
                                surname = user.documents[0].data?.get("surname").toString().replaceFirstChar { it.uppercase() },
                            )
                        }
                    }
            }
    }

}