package com.example.grocify.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocify.components.anyToInt
import com.example.grocify.components.anyToDouble
import com.example.grocify.data.CartUiState
import com.example.grocify.data.Product
import com.example.grocify.model.Cart
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class CartViewModel(application: Application): AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(CartUiState())

    val scanUiState: StateFlow<CartUiState> = _uiState.asStateFlow()


    private val db = Firebase.firestore
    private val auth = Firebase.auth


    fun getProductsList(): MutableList<Product> {
        return _uiState.value.productsList
    }

    fun getTotalPrice(): String {
        return String.format("%.2f", anyToDouble(_uiState.value.totalPrice!!)).replace(',', '.')
    }

    fun getUnitsById(id: String?): String {

        val index = _uiState.value.productsList.indexOf(_uiState.value.productsList.find { it.id == id })

        return _uiState.value.productsList[index].units.toString()
    }

    fun addValueToUnitsById(id: String?, value: Int) {

        val index = _uiState.value.productsList.indexOf(_uiState.value.productsList.find { it.id == id })

        val unitsProduct = anyToInt( _uiState.value.productsList[index].units)

        if (unitsProduct != null) {
            _uiState.value.productsList[index].units =  (unitsProduct + value).toString()
        }
    }

    fun addRow(productId: String) {
        viewModelScope.launch {
            db.collection("prodotti")
                .get()
                .addOnSuccessListener { products ->
                    for (product in products) {
                        //se il barcode scansionato corrisponde all'id di un prodotto
                        if (product.id == productId) {
                            val name = product.get("nome")?.toString() ?: ""
                            val price = product.get("prezzo_unitario")
                            val priceKg = product.get("prezzo_al_kg")
                            val quantity = product.get("quantita")?.toString() ?: ""
                            val image = product.get("immagine")?.toString() ?: ""

                            val productToAdd = Product(product.id, name, priceKg, price, quantity, image, 1)

                            _uiState.update { currentState ->
                                //se il prodotto che è stato scansionato è già presente nella lista
                                if (currentState.productsList.find { it.id == product.id } != null) {
                                    //aggiorno la quantità
                                    addValueToUnitsById(product.id, 1)
                                }
                                else {
                                    //se il prodotto non è presente nella lista, lo aggiungo
                                    currentState.productsList.add(productToAdd)
                                }
                                //aggiorno il prezzo
                                val totalPrice = anyToDouble(currentState.totalPrice)!! + anyToDouble(price)!!
                                currentState.copy(totalPrice = totalPrice.toString())
                            }
                            //salvo i cambiamenti anche nel carrelo dell'utente presente sul db
                            addToCart(productToAdd, "store")
                        }
                    }
                }
        }
    }

    fun initializeProductsList(flagCart: String) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(productsList = mutableListOf<Product>())
            }

            val cartCollection = db.collection("carts")
            //filtro solo i carrelli del negozio per l'utente attuale
            cartCollection
                .whereEqualTo("userId", auth.currentUser?.uid)
                .whereEqualTo("type", flagCart)
                .get()
                .addOnSuccessListener { documents ->
                    //se la lista di prodotti non è vuota
                    if (!documents.isEmpty) {
                        //aggiorno il prezzo del carrello
                        val totalPrice = documents.documents[0].get("totalPrice")?.toString() ?: ""
                        _uiState.update { currentState ->
                            currentState.copy(totalPrice = totalPrice)
                        }
                        val userProdRef = cartCollection.document(documents.documents[0].id)
                            .collection("products")
                        userProdRef
                            .get()
                            .addOnSuccessListener { productsList ->
                                //ciclo per i prodotti presenti nella lista
                                for (productList in productsList) {
                                    val idProductList = productList.get("id")?.toString() ?: ""
                                    val units = productList.get("quantity")
                                    db.collection("prodotti")
                                        .get()
                                        .addOnSuccessListener { products ->
                                            //ciclo per tutti i prodotti presenti in catalogo
                                            for (product in products) {
                                                //quando trovo il prodotto nel catalogo corrispondente a quello della lista
                                                //lo aggiungo a productsList
                                                if (idProductList == product.id) {
                                                    val name     = product.get("nome")?.toString() ?: ""
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
                                                        units
                                                    )

                                                    _uiState.update { currentState ->
                                                        val updatedList = currentState.productsList.orEmpty() + item
                                                        currentState.copy(productsList = updatedList.toMutableList())
                                                    }

                                                }
                                            }
                                        }
                                }
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
                        cartCollection.add(
                            Cart(
                                userId = auth.currentUser?.uid.toString(),
                                totalPrice = getTotalPrice(),
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
                                }else
                                //altrimenti aggiorno la quantità
                                    userProdRef.document(it.documents[0].id).update("quantity",
                                        FieldValue.increment(1))

                                //aggiorno il prezzo del carrello
                                /*cartCollection.document(documents.documents[0].id).update("totalPrice",
                                    FieldValue.increment(anyToDouble(product.price)!!))*/
                                cartCollection.document(documents.documents[0].id).update("totalPrice", getTotalPrice())
                            }
                    }
                }
        }
    }

    fun removeFromCart(id: String?, price: Any?, flagCart: String) {
        val cartCollection = db.collection("carts")
        viewModelScope.launch {
            //filtro solo i carrelli del negozio per l'utente attuale
            cartCollection
                .whereEqualTo("userId", auth.currentUser?.uid)
                .whereEqualTo("type", flagCart)
                .get()
                .addOnSuccessListener { documents ->
                    //se il carrello non è vuoto
                    if(!documents.isEmpty){
                        //cerco il prodotto da cancellare
                        val userProdRef = cartCollection.document(documents.documents[0].id).collection("products")
                        userProdRef
                            .whereEqualTo("id", id)
                            .get()
                            .addOnSuccessListener {
                                //se l'elemento da cancellare è presente
                                if(!it.isEmpty) {
                                    //calcolo il nuovo prezzo
                                    val priceToRemove = anyToDouble(it.documents[0].get("quantity"))?.times(anyToDouble(price)!!)
                                    //aggiorno il prezzo sul carrello prsente nel db
                                    /*cartCollection.document(documents.documents[0].id).update("totalPrice",
                                        FieldValue.increment(-priceToRemove!!))*/

                                    _uiState.update { currentState ->
                                        //aggiorno il prezzo sull'app
                                        val totalPrice = anyToDouble(currentState.totalPrice)!! - priceToRemove!!
                                        //aggiorno la lista sull'app
                                        currentState.productsList.remove(currentState.productsList.find { it.id == id } )

                                        currentState.copy(totalPrice = totalPrice.toString())
                                    }

                                    cartCollection.document(documents.documents[0].id).update("totalPrice", getTotalPrice())
                                    //rimuovo l'elemento dal carrello del db
                                    userProdRef.document(it.documents[0].id).delete()
                                }
                            }
                    }
                }
        }
    }

    fun addValueToProductUnits(id: String?, price: Any?, value: Long, flagCart: String){
        val cartCollection = db.collection("carts")
        viewModelScope.launch {
            //filtro solo i carrelli del negozio per l'utente attuale
            cartCollection
                .whereEqualTo("userId", auth.currentUser?.uid)
                .whereEqualTo("type", flagCart)
                .get()
                .addOnSuccessListener { documents ->
                    //se il carrello non è vuoto
                    if(!documents.isEmpty){
                        //ricerco il prodotto di cui voglio aumentare o decrementare le unità
                        val userProdRef = cartCollection.document(documents.documents[0].id).collection("products")
                        userProdRef
                            .whereEqualTo("id", id)
                            .get()
                            .addOnSuccessListener {
                                //se il prodotto è presente
                                if(!it.isEmpty){
                                    //aggiorno la quantità sul carrello nel db
                                    userProdRef.document(it.documents[0].id).update("quantity",  FieldValue.increment(value))

                                    //aggiorno il prezzo del carrello nel db
                                    /*cartCollection.document(documents.documents[0].id).update("totalPrice",
                                        FieldValue.increment(value * anyToDouble(price)!!))*/

                                    _uiState.update { currentState ->
                                        //aggiorno la quantità del prodotto nella lista sull'app
                                        addValueToUnitsById(id, anyToInt(value)!!)
                                        //aggiorno il prezzo del carrello nell'app
                                        val totalPrice = anyToDouble(currentState.totalPrice)!! + value * anyToDouble(price)!!

                                        currentState.copy(totalPrice = totalPrice.toString())
                                    }
                                    cartCollection.document(documents.documents[0].id).update("totalPrice", getTotalPrice())
                                }
                            }
                    }
                }
        }
    }
}
