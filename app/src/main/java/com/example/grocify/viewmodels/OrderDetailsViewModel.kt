package com.example.grocify.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocify.states.OrderDetailsUiState
import com.example.grocify.model.Product
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel class for OrderDetailsScreen.
 * @param application The application context.
 */
class OrderDetailsViewModel(application: Application): AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(OrderDetailsUiState())
    val uiState:StateFlow<OrderDetailsUiState> = _uiState.asStateFlow()

    private val db = Firebase.firestore

    /**
     * Retrieves the products associated with the given order
     * ID from the Firestore database.
     * @param orderId The ID of the order to retrieve products for.
     */
    fun getOrderProducts(orderId: String) {
        viewModelScope.launch {
            db.collection("orders")
                .whereEqualTo("orderId",orderId)
                .get()
                .addOnSuccessListener { documents ->
                    val cart:List<HashMap<String, Any>> = documents.documents[0].data?.get("cart") as List<HashMap<String, Any>>
                    val listBoolean = mutableListOf<Boolean>()
                    val productList = mutableListOf<Product>()
                    cart.forEach { product ->
                        productList.add(Product(
                            name = product["name"] as String,
                            image = product["image"] as String,
                            quantity = product["quantity"] as String,
                            units = (product["units"] as Long).toInt(),
                        )
                        )
                        listBoolean.add(false)
                    }
                    _uiState.update { it.copy(products = productList, isProductsMarked = listBoolean)}
                }
        }
    }

    /**
     * Function to set a product as marked when the driver scan to product
     * @param productId The ID of the product to mark.
     * @param orderId The ID of the order associated with the product.
     */
    fun markProduct(productId: String, orderId: String){
        val listBoolean = _uiState.value.isProductsMarked.toMutableList()
        viewModelScope.launch {
            db.collection("orders")
                .whereEqualTo("orderId", orderId)
                .get()
                .addOnSuccessListener { documents ->
                    val order = documents.documents[0]
                    val cart: List<HashMap<String, Any>> = order.data?.get("cart") as List<HashMap<String, Any>>
                    var i = 0
                    for (product in cart){
                        if(product["id"] == productId) {
                            if(order?.data!!["status"] == "in attesa"){
                                order.reference.update("status","in preparazione")
                            }
                            listBoolean[i] = true
                        }
                        i +=1
                    }
                    _uiState.update { it.copy(isProductsMarked = listBoolean) }
                }
        }
    }

}