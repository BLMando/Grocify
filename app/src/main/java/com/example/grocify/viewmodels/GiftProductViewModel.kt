package com.example.grocify.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocify.data.GiftProductUiState
import com.example.grocify.model.Product
import com.example.grocify.model.ProductType
import com.example.grocify.storage.Storage
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class GiftProductViewModel(application: Application): AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(GiftProductUiState())
    val uiState: StateFlow<GiftProductUiState> = _uiState.asStateFlow()

    private val productDao = Storage.getInstance(getApplication<Application>().applicationContext).productDao()

    private val db = Firebase.firestore
    private val auth = Firebase.auth
    fun getThresholdProducts() {
        viewModelScope.launch {
            db.collection("prodotti")
                .get()
                .addOnSuccessListener { products ->
                    if(!products.isEmpty){
                        for (product in products) {
                            val threshold = product.get("soglia")?.toString() ?: ""
                            if (threshold != "") {
                                val name = product.get("nome").toString().replaceFirstChar { it.uppercase() }
                                val priceKg = product.get("prezzo_al_kg")?.toString() ?: ""
                                val price = product.get("prezzo_unitario")?.toString() ?: ""
                                val quantity = product.get("quantita")?.toString() ?: ""
                                val image = product.get("immagine")?.toString() ?: ""
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
                                    val updatedList = currentState.thresholdProducts + item
                                    currentState.copy(thresholdProducts = updatedList.toMutableList())
                                }
                            }
                        }

                        _uiState.update { currentState ->
                            val updatedList = currentState.thresholdProducts.sortedBy { it.threshold }
                            currentState.copy(thresholdProducts = updatedList.toMutableList())
                        }
                    }
                }
        }
    }

    fun calculateMoneySpentDuringCurrentMonth(){
        viewModelScope.launch {
            db.collection("orders")
                .whereEqualTo("userId", auth.currentUser?.uid.toString())
                .get()
                .addOnSuccessListener { orders ->
                    if(!orders.isEmpty){
                        for (order in orders) {
                            val date = order.get("date")?.toString() ?: ""
                            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                            val parsedDate = LocalDate.parse(date, formatter)

                            if(!parsedDate.isBefore(_uiState.value.startOfMonth) && !parsedDate.isAfter(_uiState.value.endOfMonth)){
                                val totalPrice = order.get("totalPrice")?.toString() ?: ""
                                _uiState.update { currentState ->
                                    currentState.copy(moneySpent = currentState.moneySpent + totalPrice.toFloat())
                                }
                            }
                        }
                    }
                }
        }
    }

    fun checkIfAlreadyRedeemed(){
        viewModelScope.launch {
            db.collection("orders")
                .whereEqualTo("userId", auth.currentUser?.uid.toString())
                .get()
                .addOnSuccessListener { orders ->
                    if(!orders.isEmpty){
                        for (order in orders) {
                            val date = order.get("date")?.toString() ?: ""
                            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                            val parsedDate = LocalDate.parse(date, formatter)

                            if(!parsedDate.isBefore(_uiState.value.startOfMonth) && !parsedDate.isAfter(_uiState.value.endOfMonth)){

                                val products = order.get("cart") as? List<Map<String, Any>>

                                products?.forEach { productMap ->
                                    val threshold = productMap["threshold"] as? String
                                    Log.v("GiftProduct", threshold.toString())
                                    if(threshold != null){
                                        _uiState.update { currentState ->
                                            when(threshold) {
                                                "50" -> currentState.copy(flagThreshold50   = true)
                                                "100" -> currentState.copy(flagThreshold100 = true)
                                                "200" -> currentState.copy(flagThreshold200 = true)
                                                else -> currentState
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

    fun checkIfInCart(){
        viewModelScope.launch {
            val thresholds: List<Int> = listOf(50, 100, 200)
            val carts: List<String> = listOf("online", "store")
            for(cart in carts){
                for(threshold in thresholds){
                    val thresholdProductExists = productDao.getProductByThreshold(threshold, cart, auth.currentUser?.uid.toString())
                    if (thresholdProductExists != emptyList<Product>()) {
                        _uiState.update { currentState ->
                            when(threshold) {
                                50 -> currentState.copy(flagThreshold50   = true)
                                100 -> currentState.copy(flagThreshold100 = true)
                                200 -> currentState.copy(flagThreshold200 = true)
                                else -> currentState
                            }
                        }
                    }
                }
            }
        }
    }

    fun resetFields(){
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(thresholdProducts = mutableListOf(), moneySpent = 0.0f)
            }
        }
    }

    fun addToCart(product: ProductType, flagCart: String) {
        viewModelScope.launch {
            val name      = product.name
            val price     = product.price
            val priceKg   = product.priceKg
            val quantity  = product.quantity
            val image     = product.image
            val threshold = product.threshold

            val productToAdd = Product(product.id, flagCart, auth.currentUser?.uid.toString(), name, priceKg, price, quantity, image, 1, 0.00, threshold)

            productDao.insertProduct(productToAdd)

            _uiState.update { currentState ->
                when(threshold) {
                    50 -> currentState.copy(flagThreshold50   = true)
                    100 -> currentState.copy(flagThreshold100 = true)
                    200 -> currentState.copy(flagThreshold200 = true)
                    else -> currentState
                }
            }

        }
    }

    fun getFlagThreshold(threshold: Int): Boolean {
        var flagRedeemed = false
        when(threshold){
            50 ->  flagRedeemed = _uiState.value.flagThreshold50
            100 -> flagRedeemed = _uiState.value.flagThreshold100
            200 -> flagRedeemed = _uiState.value.flagThreshold200
        }
        return flagRedeemed
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