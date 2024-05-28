package com.example.grocify.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
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
}