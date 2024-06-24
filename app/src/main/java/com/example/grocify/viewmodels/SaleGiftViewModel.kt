package com.example.grocify.viewmodels;

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

class SaleGiftViewModel(application: Application):AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(SaleGiftUiState())
    val uiState: StateFlow<SaleGiftUiState> = _uiState.asStateFlow()

    private val db = Firebase.firestore
    fun getProducts(){
        viewModelScope.launch {
            //filtro per la categoria
            db.collection("prodotti")
                .get()
                .addOnSuccessListener { products ->
                    //se sono presenti prodotti per quella categoria
                    if(!products.isEmpty){
                        //ciclo i prodotti e li salvo in una lista
                        for (product in products) {
                            val name      = product.get("nome")?.toString() ?: ""
                            val priceKg   = product.get("prezzo_al_kg")?.toString() ?: ""
                            val price     = product.get("prezzo_unitario")?.toString() ?: ""
                            val quantity  = product.get("quantita")?.toString() ?: ""
                            val image     = product.get("immagine")?.toString() ?: ""
                            val discount  = product.get("sconto")?.toString() ?: "0.00"
                            val threshold = product.get("soglia")?.toString() ?: "0"

                            val item = ProductType(
                                product.id,
                                name,
                                priceKg.toDouble(),
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

    fun getSelectedProducts(): MutableList<ProductType>{
        return _uiState.value.selectedProducts
    }

    fun addSelectedProduct(product: ProductType){
        _uiState.update { currentState ->
            val updatedList = currentState.selectedProducts + product
            currentState.copy(selectedProducts = updatedList.toMutableList())
        }
    }

    fun removeSelectedProduct(product: ProductType){
        _uiState.update { currentState ->
            val updatedList = currentState.selectedProducts - product
            currentState.copy(selectedProducts = updatedList.toMutableList())
        }
    }

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
                val sogliaMap = hashMapOf<String, String>(
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

    fun updateDiscountProducts(discount: String) {
        if(discount != ""){
            viewModelScope.launch {
                //ciclo i prodotti per cui si vuole applicare lo sconto
                for(product in _uiState.value.selectedProducts) {
                    val discountMap = hashMapOf<String, String>(
                        "sconto" to discount
                    )
                    //aggiungo un campo al prodotto contenente lo sconto
                    db.collection("prodotti")
                        .document(product.id)
                        .update(discountMap as Map<String, String>)

                    _uiState.update { currentState ->
                        //aggiorno la lista di prodotti con il nuovo sconto
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

    fun removeDiscountedProduct(product: ProductType) {
        viewModelScope.launch {

            val removeScontoTasks = db.collection("prodotti")
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

    fun resetFields(){
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(products = mutableListOf<ProductType>(), selectedProducts = mutableListOf<ProductType>())
            }
        }
    }

}
