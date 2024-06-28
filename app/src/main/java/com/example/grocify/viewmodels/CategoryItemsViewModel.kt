package com.example.grocify.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocify.states.CategoryItemsUiState
import com.example.grocify.model.Cart
import com.example.grocify.model.Product
import com.example.grocify.model.ProductType
import com.example.grocify.data.local.Storage
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel class for CategoryItemsViewModel
 * @param application - Application context
 */
class CategoryItemsViewModel(application: Application):AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(CategoryItemsUiState())
    val uiState: StateFlow<CategoryItemsUiState> = _uiState.asStateFlow()

    private val productDao = Storage.getInstance(getApplication<Application>().applicationContext).productDao()
    private val cartDao = Storage.getInstance(getApplication<Application>().applicationContext).cartDao()

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    /**
     * Function to get the category name.
     */
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

    /**
     * Function to load the products for the category selected by the user.
     */
    fun getProducts(categoryId: String?){

        viewModelScope.launch {
            getCategoryName(categoryId)
        }

        viewModelScope.launch {
            db.collection("prodotti")
                .whereEqualTo("categoria", categoryId)
                .get()
                .addOnSuccessListener { products ->
                    if(!products.isEmpty){
                        for (product in products) {
                            val name     = product.get("nome").toString().replaceFirstChar { it.uppercase() }
                            val priceKg  = product.get("prezzo_al_kg")?.toString() ?: ""
                            val price    = product.get("prezzo_unitario")?.toString() ?: ""
                            val quantity = product.get("quantita")?.toString() ?: ""
                            val image    = product.get("immagine")?.toString() ?: ""
                            val discount  = product.get("sconto")?.toString() ?: "0.00"

                            val item = ProductType(
                                product.id,
                                name,
                                priceKg.toDouble(),
                                price.toDouble(),
                                quantity,
                                image,
                                discount.toDouble(),
                                0)

                            _uiState.update { currentState ->
                                val updatedList = currentState.products + item
                                currentState.copy(products = updatedList.toMutableList())
                            }
                        }

                    }
                    else{
                        _uiState.update {
                            it.copy(
                                isSuccessful = false
                            )
                        }
                    }

                }
        }
    }

    /**
     * Function to add a product to the products list contained in the room db, if the product wasn't already present.
     * If the product is already present it increments the units by one.
     */
    fun addToCart(product: ProductType) {
        viewModelScope.launch {

            val price = product.price

            val productToCheck = productDao.getProductByIdAndThreshold(product.id, 0, "online", auth.currentUser?.uid.toString())
            if (productToCheck != emptyList<Product>()) {
                productDao.addValueToProductUnits(product.id, 0,"online", auth.currentUser?.uid.toString(),1)
            }
            else {
                val name      = product.name
                val priceKg   = product.priceKg
                val quantity  = product.quantity
                val image     = product.image
                val discount  = product.discount

                val productToAdd = Product(
                    id = product.id,
                    type = "online",
                    userId = auth.currentUser?.uid.toString(),
                    name = name,
                    priceKg = priceKg,
                    price = price,
                    quantity = quantity,
                    image = image,
                    units = 1,
                    discount = discount,
                    threshold = 0
                )

                productDao.insertProduct(productToAdd)
            }
            cartDao.addValueToTotalPrice("online", auth.currentUser?.uid.toString(), price * (100.0 - product.discount)/100.0 )
        }
    }

    /**
     * Function to initialize the cart in the room db.
     */
    fun initializeProductsList(flagCart: String) {
        viewModelScope.launch {
            val cartDb = cartDao.getCart(flagCart, auth.currentUser?.uid.toString())
            if(cartDb == emptyList<Cart>()){
                val cart = Cart(
                    type = flagCart,
                    userId = auth.currentUser?.uid.toString(),
                    totalPrice = 1.50,
                )
                cartDao.insertCart(cart)
            }
        }
    }

    /**
     * Function to reset the ui state variables to avoid problems when page reloads.
     */
    fun resetFields(){
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(products = mutableListOf<ProductType>())
            }
        }
    }
}