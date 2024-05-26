package com.example.grocify.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocify.data.CategoryItemsUiState
import com.example.grocify.model.Cart
import com.example.grocify.model.Product
import com.example.grocify.model.ProductType
import com.example.grocify.storage.Storage
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CategoryItemsViewModel(application: Application):AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(CategoryItemsUiState())
    val uiState: StateFlow<CategoryItemsUiState> = _uiState.asStateFlow()

    val productDao = Storage.getInstance(getApplication<Application>().applicationContext).productDao()
    val cartDao = Storage.getInstance(getApplication<Application>().applicationContext).cartDao()

    private val db = Firebase.firestore

    private fun getCategoryName(categoryId: String?) {
        db.collection("categories")
            .whereEqualTo(FieldPath.documentId(),categoryId)
            .get()
            .addOnSuccessListener {
                _uiState.update { currentState ->
                    currentState.copy(
                        categoryName = it.documents[0].get("nome").toString()
                    )
                }
            }
    }

    fun getProducts(categoryId: String?){
        //estraggo la categoria
        viewModelScope.launch {
            getCategoryName(categoryId)
        }

        viewModelScope.launch {
            //filtro per la categoria
            db.collection("prodotti")
                .whereEqualTo("categoria", categoryId)
                .get()
                .addOnSuccessListener { products ->
                    //se sono presenti prodotti per quella categoria
                    if(!products.isEmpty){
                        //ciclo i prodotti e li salvo in una lista
                        for (product in products) {
                            val name     = product.get("nome").toString().replaceFirstChar { it.uppercase() }
                            val priceKg  = product.get("prezzo_al_kg")?.toString() ?: ""
                            val price    = product.get("prezzo_unitario")?.toString() ?: ""
                            val quantity = product.get("quantita")?.toString() ?: ""
                            val image    = product.get("immagine")?.toString() ?: ""

                           val item = ProductType(
                                product.id,
                                name,
                                priceKg.toDouble(),
                                price.toDouble(),
                                quantity,
                                image)

                            _uiState.update { currentState ->
                                val updatedList = currentState.products + item
                                currentState.copy(products = updatedList.toMutableList())
                            }
                        }

                    }
                    else{//altrimenti segnalo che non sono presenti prodotti nella lista
                        _uiState.update {
                            it.copy(
                                isSuccessful = false
                            )
                        }
                    }

                }
        }
    }

    fun addToCart(productId: String) {
        viewModelScope.launch {
            //scorro tutti i prodotti
            db.collection("prodotti")
                .get()
                .addOnSuccessListener { products ->
                    for (product in products) {
                        if (product.id == productId) {
                            val price = product.get("prezzo_unitario")?.toString() ?: ""

                            val productToCheck = productDao.getProductById(product.id, "online")
                            if (productToCheck != emptyList<Product>()) {
                                productDao.addValueToProductUnits(1, product.id, "online")
                            }
                            else {
                                val name = product.get("nome")?.toString() ?: ""
                                val priceKg = product.get("prezzo_al_kg")?.toString() ?: ""
                                val quantity = product.get("quantita")?.toString() ?: ""
                                val image = product.get("immagine")?.toString() ?: ""

                                val productToAdd = Product(product.id, "online", name, priceKg.toDouble(), price.toDouble(), quantity, image, 1)

                                productDao.insertProduct(productToAdd)
                            }
                            cartDao.addValueToTotalPrice("online", price.toDouble())
                        }
                    }
                }
        }
    }

    fun initializeProductsList(flagCart: String) {
        viewModelScope.launch {
            val cartDb = cartDao.getCart(flagCart)
            if(cartDb == emptyList<Cart>()){
                val cart = Cart(
                    type = flagCart,
                    totalPrice = 1.50,
                )
                cartDao.insertCart(cart)
            }
        }
    }
}