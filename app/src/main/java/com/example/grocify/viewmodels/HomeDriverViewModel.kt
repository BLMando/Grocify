package com.example.grocify.viewmodels

import android.app.Application

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocify.states.HomeDriverUiState
import com.example.grocify.model.Order
import com.example.grocify.utils.parseDate
import com.example.grocify.utils.parseTime
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.SortedMap
import java.util.TreeMap
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
     * Function to get the pending orders and the orders in subsequent states for the current driver
     * in the current date and update the UI state accordingly using a snapshot listener.
     * @see Order
     */
    fun getOrders(){
        // Sorted map to store orders based on status and date/time
        // TreeMap ensures that sorting based on the key is automatically maintained."
        val ordersMap: SortedMap<String, Order> = TreeMap()

        viewModelScope.launch {

            val pendingOrdersQuery = db.collection("orders")
                .whereEqualTo("status", "in attesa")

            val runningOrdersQuery = db.collection("orders")
                .whereIn("status", listOf("in preparazione","in consegna", "consegnato"))
                .whereEqualTo("driverId", getCurrentDriverId())

            pendingOrdersQuery.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val pendingOrders = snapshot.documents.mapNotNull { it.toObject(Order::class.java) }
                    updateOrdersList(pendingOrders,ordersMap)
                }
            }

            runningOrdersQuery.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val runningOrders = snapshot.documents.mapNotNull { it.toObject(Order::class.java) }
                    updateOrdersList(runningOrders, ordersMap)
                }
            }
        }
    }
    /**
    Function to update the orders list in the UI state.
    This function is synchronized to ensure thread safety.
    @param newOrders - List of new orders
    @param ordersMap - Current list of orders
     */
    @Synchronized
    private fun updateOrdersList(newOrders: List<Order>, ordersMap: MutableMap<String, Order>) {

        val orderStates = listOf("consegnato", "in consegna", "in preparazione", "in attesa")

        newOrders.forEach { newOrder ->
            if (ordersMap.containsKey(newOrder.orderId)) {
                ordersMap[newOrder.orderId] = newOrder
            } else {
                ordersMap[newOrder.orderId] = newOrder
            }
        }

        /**
         * Sort the orders based on the following criteria:
         * 1. Order status (consegnato > in consegna > in preparazione > in attesa)
         * 2. Order date
         * 3. Order time
         */
        val sortedOrders = ordersMap.values.sortedWith(
            compareBy<Order> { orderStates.indexOf(it.status) }
                .thenBy { parseDate(it.date) }
                .thenBy { parseTime(it.time) }
        )

        _uiState.update { currentState ->
            currentState.copy(orders = sortedOrders)
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
    override suspend fun setOrderConclude(orderId: String) {
        val document = db.collection("orders")
            .whereEqualTo("orderId", orderId)
            .get()
            .await()

        val order = document.documents[0].reference
        order.update("status", "concluso").await()
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