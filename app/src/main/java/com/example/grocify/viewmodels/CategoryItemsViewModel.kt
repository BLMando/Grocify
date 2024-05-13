package com.example.grocify.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocify.data.CategoryItemsUiState
import com.example.grocify.data.Product
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class CategoryItemsViewModel(application: Application):AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(CategoryItemsUiState())
    val uiState: StateFlow<CategoryItemsUiState> = _uiState.asStateFlow()

    private val db = Firebase.firestore

    fun getProducts(categoryId: String?){
        val products: MutableList<Product> = mutableListOf()

        viewModelScope.launch {
            getCategoryName(categoryId)
        }

        viewModelScope.launch {
            db.collection("prodotti")
                .whereEqualTo("categoria",categoryId)
                .get()
                .addOnSuccessListener { documents ->
                    if(!documents.isEmpty){
                        for (document in documents) {
                            Log.d("prodformcat",document.toString())
                            val name = document.get("nome").toString().replaceFirstChar { it.uppercase() }
                            val quantity = document.get("quantita").toString()
                            val price = document.get("prezzo_unitario")
                            val priceKg = document.get("prezzo_al_kg")
                            val image = document.get("immagine").toString()
                            products.add(Product(name,priceKg,price,quantity,image))
                        }
                        _uiState.update {
                            it.copy(
                                products = products.toList(),
                            )
                        }
                    }else
                        _uiState.update {
                            it.copy(
                                isSuccessful = false
                            )
                        }
                }
        }
    }

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

    fun addToCart(product: Product){
       Log.d("addtocart", product.toString())
    }

}