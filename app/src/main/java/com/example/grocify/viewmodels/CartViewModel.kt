package com.example.grocify.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocify.data.CartUiState
import com.example.grocify.model.Cart
import com.example.grocify.model.Product
import com.example.grocify.storage.Storage
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

    fun getUnitsById(id: String?): String {

        val index = _uiState.value.productsList.indexOf(_uiState.value.productsList.find { it.id == id })

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
                            val name = product.get("nome")?.toString() ?: ""
                            val price = product.get("prezzo_unitario")?.toString() ?: ""
                            val priceKg = product.get("prezzo_al_kg")?.toString() ?: ""
                            val quantity = product.get("quantita")?.toString() ?: ""
                            val image = product.get("immagine")?.toString() ?: ""

                            val productToAdd = Product(product.id, "store", auth.currentUser?.uid.toString(), name, priceKg.toDouble(), price.toDouble(), quantity, image, 1)

                            _uiState.update { currentState ->
                                val updatedList = currentState.productsList.toMutableList()
                                val existingProduct = updatedList.find { it.id == product.id }
                                if (existingProduct != null) {
                                    existingProduct.units += 1
                                }
                                else {
                                    productDao.insertProduct(productToAdd)
                                    updatedList.add(productToAdd)
                                }
                                cartDao.addValueToTotalPrice("store", auth.currentUser?.uid.toString(), price.toDouble())
                                val totalPrice = currentState.totalPrice + price.toDouble()
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
                    totalPrice = if(flagCart == "store") 0.00 else  1.50,
                )
                cartDao.insertCart(cart)
                _uiState.update { currentState ->
                    currentState.copy(totalPrice = if(flagCart == "store") 0.00 else  1.50)
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

    fun removeFromCart(id: String, price: Double, units: Int, flagCart: String) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                val updatedList = currentState.productsList.toMutableList()
                val productToRemove = updatedList.find { it.id == id }
                if (productToRemove != null) {
                    productDao.deleteById(id, flagCart, auth.currentUser?.uid.toString())
                    cartDao.addValueToTotalPrice(flagCart, auth.currentUser?.uid.toString(),-(units * price))
                    updatedList.remove(productToRemove)
                    val totalPrice = currentState.totalPrice - (units * price)
                    currentState.copy(productsList = updatedList, totalPrice = totalPrice)
                }
                else {
                    currentState
                }
            }
        }
    }

    fun addValueToProductUnits(id: String, price: Double, value: Int, flagCart: String) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                val updatedList = currentState.productsList.toMutableList()
                val product = updatedList.find { it.id == id }
                if (product != null) {
                    productDao.addValueToProductUnits(id, flagCart, auth.currentUser?.uid.toString(), value)
                    cartDao.addValueToTotalPrice(flagCart, auth.currentUser?.uid.toString(),value * price)
                    product.units += value
                    val totalPrice = currentState.totalPrice + value * price
                    currentState.copy(productsList = updatedList, totalPrice = totalPrice)
                }
                else {
                    currentState
                }
            }
        }
    }
}
