package com.example.grocify.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocify.states.SaleGiftUiState
import com.example.grocify.model.ProductType
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * ViewModel class for SaleGiftViewModel
 * @param application - Application context
 */
class SaleGiftViewModel(application: Application):AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(SaleGiftUiState())
    val uiState: StateFlow<SaleGiftUiState> = _uiState.asStateFlow()

    private val db = Firebase.firestore

    /**
     * Function to load all the products.
     */
    fun getProducts(){
        viewModelScope.launch {
            db.collection("prodotti")
                .get()
                .addOnSuccessListener { products ->
                    if(!products.isEmpty){

                        for (product in products) {
                            val name      = product.get("nome")?.toString() ?: ""
                            val price     = product.get("prezzo_unitario")?.toString() ?: ""
                            val quantity  = product.get("quantita")?.toString() ?: ""
                            val image     = product.get("immagine")?.toString() ?: ""
                            val discount  = product.get("sconto")?.toString() ?: "0.00"
                            val threshold = product.get("soglia")?.toString() ?: "0"

                            val item = ProductType(
                                product.id,
                                name,
                                price.toDouble(),
                                quantity,
                                image,
                                discount.toDouble(),
                                threshold.toInt())

                            _uiState.update { currentState ->
                                val updatedList = currentState.products + item
                                currentState.copy(products = updatedList.toMutableList())
                            }
                        }

                    }
                }
        }
    }

    /**
     * Function to load the three free products.
     */
    fun initializeSelectedProducts(){
            viewModelScope.launch {
                db.collection("prodotti")
                    .get()
                    .addOnSuccessListener { products ->
                        if(!products.isEmpty){
                            for (product in products) {
                                val threshold = product.get("soglia")?.toString() ?: ""
                                if (threshold != "") {
                                    val name     = product.get("nome")?.toString() ?: ""
                                    val price    = product.get("prezzo_unitario")?.toString() ?: ""
                                    val quantity = product.get("quantita")?.toString() ?: ""
                                    val image    = product.get("immagine")?.toString() ?: ""
                                    val discount  = product.get("sconto")?.toString() ?: "0.00"

                                    val item = ProductType(
                                        product.id,
                                        name,
                                        price.toDouble(),
                                        quantity,
                                        image,
                                        discount.toDouble(),
                                        threshold.toInt()
                                    )

                                    _uiState.update { currentState ->
                                        val updatedList = currentState.selectedProducts + item
                                        currentState.copy(selectedProducts = updatedList.toMutableList())
                                    }
                                }
                            }

                            _uiState.update { currentState ->
                                val updatedList = currentState.selectedProducts.sortedBy { it.threshold }
                                currentState.copy(selectedProducts = updatedList.toMutableList())
                            }

                        }
                    }


            }
    }

    /**
     * Function to get the selected products list.
     */
    fun getSelectedProducts(): MutableList<ProductType>{
        return _uiState.value.selectedProducts
    }

    /**
     * Function to add a product to the selected products list.
     */
    fun addSelectedProduct(product: ProductType){
        _uiState.update { currentState ->
            val updatedList = currentState.selectedProducts + product
            currentState.copy(selectedProducts = updatedList.toMutableList())
        }
    }

    /**
     * Function to remove a product from the selected products list.
     */
    fun removeSelectedProduct(product: ProductType){
        _uiState.update { currentState ->
            val updatedList = currentState.selectedProducts - product
            currentState.copy(selectedProducts = updatedList.toMutableList())
        }
    }

    /**
     * Function to update the three free products.
     */
    fun updateThresholdProducts() {
        viewModelScope.launch {

            val removeSogliaTasks = db.collection("prodotti")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot.documents) {
                        document.reference.update("soglia", FieldValue.delete())
                    }
                }
            removeSogliaTasks.await()

            for(product in _uiState.value.selectedProducts) {
                val sogliaMap = hashMapOf(
                    "soglia" to when(_uiState.value.selectedProducts.indexOf(product)) {
                        0 -> "50"
                        1 -> "100"
                        2 -> "200"
                        else -> ""
                    }
                )
                db.collection("prodotti")
                    .document(product.id)
                    .update(sogliaMap as Map<String, String>)
            }

        }
    }

    /**
     * Function to set a discount for every product selected.
     */
    fun updateDiscountProducts(discount: String) {
        if(discount != ""){
            viewModelScope.launch {

                for(product in _uiState.value.selectedProducts) {
                    val discountMap = hashMapOf(
                        "sconto" to discount
                    )

                    db.collection("prodotti")
                        .document(product.id)
                        .update(discountMap as Map<String, String>)

                    _uiState.update { currentState ->

                        val updatedList = currentState.products
                        updatedList[updatedList.indexOf(product)].discount = discount.toDouble()
                        currentState.copy(products = updatedList)
                    }
                }
                _uiState.update { currentState ->
                    currentState.copy(selectedProducts = emptyList<ProductType>().toMutableList())
                }

            }
        }
    }

    /**
     * Function to remove the discount from every product selected.
     */
    fun removeDiscountedProduct(product: ProductType) {
        viewModelScope.launch {

            db.collection("prodotti")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot.documents) {
                        if(document.id == product.id){
                            document.reference.update("sconto", FieldValue.delete())
                        }
                    }
                }

            _uiState.value.products[_uiState.value.products.indexOf(product)].discount = 0.00

        }
    }

    /**
     * Function to reset the ui state variables to avoid problems when page reloads.
     */
    fun resetFields(){
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(products = mutableListOf(), selectedProducts = mutableListOf())
            }
        }
    }

}
