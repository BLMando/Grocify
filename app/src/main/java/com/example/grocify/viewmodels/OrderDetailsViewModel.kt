package com.example.grocify.viewmodels

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocify.R
import com.example.grocify.data.OrderDetailsUiState
import com.example.grocify.model.Product
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OrderDetailsViewModel(application: Application): AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(OrderDetailsUiState())
    val uiState:StateFlow<OrderDetailsUiState> = _uiState.asStateFlow()

    private val db = Firebase.firestore

    fun getOrderProducts(orderId: String) {
        viewModelScope.launch {
            db.collection("orders")
                .whereEqualTo("orderId",orderId)
                .get()
                .addOnSuccessListener { documents ->
                    val cart:List<HashMap<String, Any>> = documents.documents[0].data?.get("cart") as List<HashMap<String, Any>>
                    val productList = mutableListOf<Product>()
                    cart.forEach { product ->
                        productList.add(Product(
                            name = product["name"] as String,
                            image = product["image"] as String,
                            quantity = product["quantity"] as String,
                            units = (product["units"] as Long).toInt(),
                        ))
                    }
                    _uiState.update { it.copy(products = productList)}
                }
        }
    }


    fun markProduct(productId: String, orderId: String){
        val listBoolean = mutableListOf<Boolean>()
        viewModelScope.launch {
            db.collection("orders")
                .whereEqualTo("orderId", orderId)
                .get()
                .addOnSuccessListener { documents ->
                    val order = documents.documents[0]
                    val cart: List<HashMap<String, Any>> = order.data?.get("cart") as List<HashMap<String, Any>>
                    for (product in cart){
                        if(product["id"] == productId) {
                            /*if(order?.data!!["status"] == "in attesa"){
                                order.reference.update("status","in preparazione")
                                sendNotification()
                            }*/
                            listBoolean.add(true)
                        }else
                            listBoolean.add(false)
                    }
                    _uiState.update { it.copy(isProductsMarked = listBoolean) }
                }
        }
    }

    private fun sendNotification(){
        val notification =
            NotificationCompat.Builder(getApplication<Application>().applicationContext, "OrderStatusChannel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Ordine in preparazione")
                .setContentText("Il tuo ordine è stato preso in carico da un driver che si occuperà della spesa")
                .build()
        val notificationManager =
            getApplication<Application>().applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
    }
}