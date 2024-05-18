package com.example.grocify.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.grocify.data.CartUiState
import com.example.grocify.model.Product
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CartViewModel(application: Application): AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    fun getCartItem(){
        val productsList: MutableList<Product> = mutableListOf()
        val cartCollection = db.collection("carts")

        cartCollection
            .whereEqualTo("userId",auth.currentUser?.uid)
            .whereEqualTo("type", "online")
            .get()
            .addOnSuccessListener {
                cartCollection
                    .document(it.documents[0].id)
                    .collection("products")
                    .get()
                    .addOnSuccessListener { products ->
                        if(!products.isEmpty){
                            for (product in products){
                                val name = product.get("nome").toString().replaceFirstChar { it.uppercase() }
                                val quantity = product.get("quantita").toString()

                                val price:Any = product.get("prezzo_unitario") as Any
                                var _price =  0.0
                                if (price is Long)
                                    _price = price.toDouble()
                                else if (price is Double)
                                    _price = price.toDouble()

                                productsList.add(Product(name,null,_price,quantity,null))
                            }
                            _uiState.update { currentState ->
                                currentState.copy(
                                    products = productsList
                                )
                            }

                        }else
                            _uiState.update{ currentState ->
                                currentState.copy(
                                    isSuccessful = false
                                )
                            }
                    }
            }
    }
}


