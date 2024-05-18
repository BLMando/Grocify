package com.example.grocify.viewmodels

import android.app.Application
import android.util.Log
import androidx.collection.MutableObjectList
import androidx.collection.ObjectList
import androidx.collection.emptyObjectList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocify.data.CategoryItemsUiState
import com.example.grocify.model.Cart
import com.example.grocify.model.Product
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Filter
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

    private val db = Firebase.firestore
    private val auth = Firebase.auth

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
                            val name = document.get("nome").toString().replaceFirstChar { it.uppercase() }
                            val quantity = document.get("quantita").toString()

                            val price:Any = document.get("prezzo_unitario") as Any
                            var _price =  0.0
                            if (price is Long)
                                _price = price.toDouble()
                            else if (price is Double)
                                _price = price.toDouble()

                            val priceKg:Any = document.get("prezzo_al_kg") as Any
                            var _priceKg = 0.0
                            if (priceKg is Long)
                                _priceKg = priceKg.toDouble()
                            else if (priceKg is Double)
                                _priceKg = priceKg.toDouble()

                            val image = document.get("immagine").toString()
                            products.add(Product(name,_priceKg,_price,quantity,image))
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
       val cartCollection = db.collection("carts")
        viewModelScope.launch {
            //filtro solo i carrelli online per l'utente attuale
            cartCollection
                .whereEqualTo("userId", auth.currentUser?.uid)
                .whereEqualTo("type", "online")
                .get()
                .addOnSuccessListener { documents ->
                   if(documents.isEmpty){
                       //se non ho già un carrello per l'utente lo creo
                       val addedProduct = hashMapOf(
                           "name" to product.name,
                           "price" to product.price,
                           "quantity" to 1,
                       )
                       cartCollection.add(
                           Cart(
                               userId = auth.currentUser?.uid.toString(),
                               totalPrice = product.price,
                               type = "online",
                           )
                       ).addOnSuccessListener {
                           //appena creato il carrello crea la subcollection con i prodotti
                           cartCollection
                               .document(it.id)
                               .collection("products")
                               .add(addedProduct)
                       }

                   }else{
                       //se ho già il carrello aggiungo il prodotto alla subcollection
                       val userProdRef = cartCollection.document(documents.documents[0].id).collection("products")
                       userProdRef
                           .whereEqualTo("name",product.name)
                           .get()
                           .addOnSuccessListener {
                               if(it.isEmpty){
                                   //se l'utente non ha ancora inserito il prodotto che ha cliccato allora lo aggiungo
                                   val newAddedProduct = hashMapOf(
                                       "name" to product.name,
                                       "price" to product.price,
                                       "quantity" to 1,
                                   )
                                   userProdRef.add(newAddedProduct)
                               }else
                                   //altrimenti aggiorno la quantità
                                   userProdRef.document(it.documents[0].id).update("quantity",FieldValue.increment(1))

                                //aggiorno il prezzo del carrello
                               cartCollection.document(documents.documents[0].id).update("totalPrice",FieldValue.increment(product.price))
                           }
                   }
               }
            }
    }
}