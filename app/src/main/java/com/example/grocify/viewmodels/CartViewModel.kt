package com.example.grocify.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocify.states.CartUiState
import com.example.grocify.model.Cart
import com.example.grocify.model.Product
import com.example.grocify.data.local.Storage
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class CartViewModel(application: Application): AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    private val productDao = Storage.getInstance(getApplication<Application>().applicationContext).productDao()
    private val cartDao = Storage.getInstance(getApplication<Application>().applicationContext).cartDao()

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    fun getUnitsByIdAndThreshold(id: String?, threshold: Int): String {

        val index = _uiState.value.productsList.indexOf(_uiState.value.productsList.find { it.id == id && it.threshold == threshold })

        return _uiState.value.productsList[index].units.toString()
    }


    fun addRow(productId: String) {
        viewModelScope.launch {
            //scorro tutti i prodotti
            db.collection("prodotti")
                .get()
                .addOnSuccessListener { products ->
                    for (product in products) {
                        if (product.id == productId) {
                            val name     = product.get("nome")?.toString() ?: ""
                            val price    = product.get("prezzo_unitario")?.toString() ?: ""
                            val priceKg  = product.get("prezzo_al_kg")?.toString() ?: ""
                            val quantity = product.get("quantita")?.toString() ?: ""
                            val image    = product.get("immagine")?.toString() ?: ""
                            val discount = product.get("sconto")?.toString() ?: "0.00"

                            val productToAdd = Product(
                                id = product.id,
                                type = "store",
                                userId = auth.currentUser?.uid.toString(),
                                name = name,
                                priceKg = priceKg.toDouble(),
                                price = price.toDouble(),
                                quantity = quantity,
                                image = image,
                                units = 1,
                                discount = discount.toDouble(),
                                0
                            )

                            _uiState.update { currentState ->
                                val updatedList = currentState.productsList.toMutableList()
                                val existingProduct = updatedList.find { it.id == product.id && it.threshold == 0 }
                                if (existingProduct != null) {
                                    existingProduct.units += 1
                                    productDao.addValueToProductUnits(product.id, 0,"store", auth.currentUser?.uid.toString(),1)
                                }
                                else {
                                    updatedList.add(productToAdd)
                                    productDao.insertProduct(productToAdd)
                                }
                                cartDao.addValueToTotalPrice("store", auth.currentUser?.uid.toString(), (price.toDouble() * (100.0 - discount.toDouble())/100.0))
                                val totalPrice = currentState.totalPrice + (price.toDouble() * (100.0 - discount.toDouble())/100.0)
                                currentState.copy(productsList = updatedList, totalPrice = totalPrice)
                            }

                        }
                    }
                }
        }
    }

    fun initializeProductsList(flagCart: String) {
        viewModelScope.launch {

            val products = productDao.getProducts(flagCart, auth.currentUser?.uid.toString())
            val cartDb = cartDao.getCart(flagCart, auth.currentUser?.uid.toString())

            if(cartDb == emptyList<Cart>()){
                val cart = Cart(
                    type = flagCart,
                    userId = auth.currentUser?.uid.toString(),
                    totalPrice = if(flagCart == "store") 0.00 else 1.50,
                )
                cartDao.insertCart(cart)
                _uiState.update { currentState ->
                    currentState.copy(totalPrice = if(flagCart == "store") 0.00 else 1.50)
                }
            }
            else{
                _uiState.update { currentState ->
                    currentState.copy(totalPrice= cartDb[0].totalPrice)
                }
            }

            _uiState.update { currentState ->
                currentState.copy(productsList = products)
            }

        }
    }

    fun removeFromCart(product: Product, flagCart: String) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                val updatedList = currentState.productsList.toMutableList()
                val productToRemove = updatedList.find { it.id == product.id && it.threshold == product.threshold }
                if (productToRemove != null) {
                    productDao.deleteByIdAndThreshold(product.id, product.threshold, flagCart, auth.currentUser?.uid.toString())
                    updatedList.remove(productToRemove)
                    if(product.threshold == 0){
                        cartDao.addValueToTotalPrice(flagCart, auth.currentUser?.uid.toString(),-(product.units * (product.price * (100.0 - product.discount)/100.0)))
                        val totalPrice = currentState.totalPrice - (product.units * (product.price * (100.0 - product.discount)/100.0))
                        currentState.copy(productsList = updatedList, totalPrice = totalPrice)
                    }
                    else{
                        currentState.copy(productsList = updatedList)
                    }

                }
                else {
                    currentState
                }
            }
        }
    }

    fun addValueToProductUnits(product: Product, value: Int, flagCart: String) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                val updatedList = currentState.productsList.toMutableList()
                val existingProduct = updatedList.find { it.id == product.id && it.threshold == product.threshold }
                if (existingProduct != null) {
                    productDao.addValueToProductUnits(product.id, product.threshold, flagCart, auth.currentUser?.uid.toString(), value)
                    cartDao.addValueToTotalPrice(flagCart, auth.currentUser?.uid.toString(),value * (product.price * (100.0 - product.discount)/100.0))
                    product.units += value
                    val totalPrice = currentState.totalPrice + value * (product.price * (100.0 - product.discount)/100.0)
                    currentState.copy(productsList = updatedList, totalPrice = totalPrice)
                }
                else {
                    currentState
                }
            }
        }
    }

    fun checkOrdersStatus(){
        db.collection("orders")
            .whereEqualTo("userId", auth.currentUser!!.uid)
            .whereNotEqualTo("status", "concluso")
            .addSnapshotListener { documentSnapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }

                if (documentSnapshot!!.documents.isEmpty()) {
                    _uiState.update {
                        it.copy(orderId = "")
                    }
                }
                else {
                    val orderId = documentSnapshot.documents[0].get("orderId").toString()
                    _uiState.update {
                        it.copy(orderId = orderId)
                    }
                }
            }
    }
}
