package com.example.grocify.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocify.data.CategoryItemsUiState
import com.example.grocify.model.Cart
import com.example.grocify.model.Product
import com.example.grocify.util.anyToDouble
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

    private val db = Firebase.firestore
    private val auth = Firebase.auth

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
                            val priceKg  = product.get("prezzo_al_kg")
                            val price    = product.get("prezzo_unitario")
                            val quantity = product.get("quantita")?.toString() ?: ""
                            val image    = product.get("immagine")?.toString() ?: ""

                           val item = Product(
                                product.id,
                                name,
                                priceKg,
                                price,
                                quantity,
                                image,
                                1
                            )

                            _uiState.update { currentState ->
                                val updatedList = currentState.products.orEmpty() + item
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

    fun addToCart(product: Product, flagCart: String){
        val cartCollection = db.collection("carts")
        viewModelScope.launch {
            //filtro solo i carrelli del negozio per l'utente attuale
            cartCollection
                .whereEqualTo("userId", auth.currentUser?.uid)
                .whereEqualTo("type", flagCart)
                .get()
                .addOnSuccessListener { documents ->
                    if(documents.isEmpty){
                        //se non ho già un carrello per l'utente lo creo
                        val addedProduct = hashMapOf(
                            "id" to product.id,
                            "quantity" to 1,
                        )

                        addToTotalPrice(product.price)

                        cartCollection.add(
                            Cart(
                                userId = auth.currentUser?.uid.toString(),
                                totalPrice = _uiState.value.totalPrice,
                                type = flagCart,
                            )
                        ).addOnSuccessListener {
                            //appena creato il carrello crea la subcollection con i prodotti
                            cartCollection
                                .document(it.id)
                                .collection("products")
                                .add(addedProduct)
                        }

                    }
                    else{
                        //se ho già il carrello aggiungo il prodotto alla subcollection
                        val userProdRef = cartCollection.document(documents.documents[0].id).collection("products")
                        userProdRef
                            .whereEqualTo("id",product.id)
                            .get()
                            .addOnSuccessListener {
                                if(it.isEmpty){
                                    //se l'utente non ha ancora inserito il prodotto che ha cliccato allora lo aggiungo
                                    val newAddedProduct = hashMapOf(
                                        "id" to product.id,
                                        "quantity" to 1,
                                    )
                                    userProdRef.add(newAddedProduct)
                                }
                                else{
                                    //altrimenti aggiorno la quantità
                                    userProdRef.document(it.documents[0].id).update("quantity",
                                        FieldValue.increment(1))
                                }

                                //aggiorno il prezzo del carrello
                                /*cartCollection.document(documents.documents[0].id).update("totalPrice",
                                    FieldValue.increment(anyToDouble(product.price)!!))*/
                                addToTotalPrice(product.price)
                                cartCollection.document(documents.documents[0].id).update("totalPrice", _uiState.value.totalPrice)

                            }
                    }
                }
        }
    }

    fun getTotalPrice(){
        val cartCollection = db.collection("carts")
        viewModelScope.launch {
            //filtro solo i carrelli del negozio per l'utente attuale
            cartCollection
                .whereEqualTo("userId", auth.currentUser?.uid)
                .get()
                .addOnSuccessListener { documents ->
                    if(!documents.isEmpty) {
                        _uiState.value.totalPrice = documents.documents[0].get("totalPrice")?.toString()!!
                    }
                }
        }
    }

    fun addToTotalPrice(priceToAdd: Any?){
        val newPrice = anyToDouble(_uiState.value.totalPrice)!! + anyToDouble(priceToAdd)!!
        _uiState.value.totalPrice = String.format("%.2f", anyToDouble(newPrice)).replace(',', '.')
        Log.v("ciao",_uiState.value.totalPrice)
    }
}