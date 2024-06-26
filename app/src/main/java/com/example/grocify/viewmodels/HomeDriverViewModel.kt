package com.example.grocify.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocify.states.HomeDriverUiState
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

/**
 * ViewModel class for HomeDriverScreen
 * @param application - Application context
 */
class HomeDriverViewModel(application: Application,private val mOneTapClient: SignInClient):AndroidViewModel(application), MapDialog {

    private val _uiState = MutableStateFlow(HomeDriverUiState())
    val uiState:StateFlow<HomeDriverUiState> = _uiState.asStateFlow()

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    fun getCurrentDriverId() = auth.currentUser?.uid

    /**
     * Function to get the name of the signed in driver
     */
    fun getSignedInDriverName() {
        val currentUser = auth.currentUser?.uid
        viewModelScope.launch {
            db.collection("users")
                .whereEqualTo("uid", currentUser)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        _uiState.update { currentState ->
                            currentState.copy(currentUserName = document.data["name"].toString().replaceFirstChar { it.uppercase() })
                        }
                    }
                }
        }
    }

    /**
     * Function to get the pending orders and preparation orders for the current driver
     * in the current date and update the UI state accordingly using a snapshot listener.
     * @see Order
     */
    fun getOrders(){
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        val ordersList: MutableList<Order> = mutableListOf()

        viewModelScope.launch {

            val pendingOrdersQuery = db.collection("orders")
                .whereEqualTo("date", currentDate.format(formatter) )
                .whereEqualTo("status", "in attesa")

            val runningOrdersQuery = db.collection("orders")
                .whereEqualTo("date", currentDate.format(formatter) )
                .whereIn("status", listOf("in preparazione","in consegna", "consegnato"))
                .whereEqualTo("driverId", getCurrentDriverId())

            pendingOrdersQuery.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val pendingOrders = snapshot.documents.mapNotNull { it.toObject(Order::class.java) }
                    updateOrdersList(pendingOrders,ordersList)
                }
            }

            runningOrdersQuery.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val preparationOrders = snapshot.documents.mapNotNull { it.toObject(Order::class.java) }
                    updateOrdersList(preparationOrders,ordersList)
                }
            }

        }
    }

    /**

    Function to update the orders list in the UI state.
    This function is synchronized to ensure thread safety.
    @param newOrders - List of new orders
    @param ordersList - Current list of orders
     */
    @Synchronized
    private fun updateOrdersList(newOrders: List<Order>, ordersList: MutableList<Order>) {
        ordersList.addAll(newOrders)

        _uiState.update { currentState ->
            currentState.copy(orders = ordersList.toList())
        }
    }



    /**
     * Function to set the dialog state of the MapDialog
     * @param state - Boolean value to set the dialog state
     */
    override fun setDialogState(state: Boolean) {
        _uiState.update { it.copy(openDialog = state) }
    }

    /**
     * Function to set the order status to "concluso" after driver scanned the QR code
     * @param orderId - ID of the order to be concluded
     */
    override fun setOrderConclude(orderId: String) {
        viewModelScope.launch {
            db.collection("orders")
                .whereEqualTo("orderId", orderId)
                .get()
                .addOnSuccessListener { document ->
                    val order = document.documents[0].reference
                    order.update("status", "concluso")
                }
        }
    }


    /**
     * Function to sign out the user
     * and clear the auth state
     */
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