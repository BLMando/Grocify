package com.example.grocify.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocify.states.TrackOrderUiState
import com.example.grocify.model.Order
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel class for TrackOrderScreen.
 * @param application The application context.
 */
class TrackOrderViewModel(application: Application): AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(TrackOrderUiState())
    val uiState:StateFlow<TrackOrderUiState> = _uiState.asStateFlow()

    private val db = Firebase.firestore


    /**
     * Retrieves the driver name associated with the given order Id
     * from the Firestore database.
     * @param orderId The ID of the order to retrieve the driver name for.
     */
    fun getDriverName(orderId: String) {
        viewModelScope.launch {
            db.collection("orders")
                .whereEqualTo("orderId", orderId)
                .get()
                .addOnSuccessListener { documents ->
                    val driverId = documents.documents[0].data?.get("driverId") as String
                    db.collection("users")
                        .whereEqualTo("uid", driverId)
                        .get()
                        .addOnSuccessListener { user ->
                            val driverName = user.documents[0].data?.get("name") as String
                            val driverSurname = user.documents[0].data?.get("surname") as String

                            _uiState.update { it.copy(driverName = "$driverName $driverSurname") }
                        }

                }
        }
    }

    /**
     * Get current order from firestore
     * @param orderId String
     */
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

}