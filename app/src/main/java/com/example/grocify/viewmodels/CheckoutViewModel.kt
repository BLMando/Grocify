package com.example.grocify.viewmodels

import com.example.grocify.api.FirebaseCloudMessaging.FCMResponse
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocify.api.FirebaseCloudMessaging.FCMNotification
import com.example.grocify.api.FirebaseCloudMessaging.Message
import com.example.grocify.api.FirebaseCloudMessaging.Notification
import com.example.grocify.api.RetrofitObject
import com.example.grocify.data.CheckoutUiState
import com.example.grocify.model.Address
import com.example.grocify.model.Order
import com.example.grocify.model.PaymentMethod
import com.example.grocify.storage.Storage
import com.example.grocify.util.maskCardNumber
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.RoundingMode
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CheckoutViewModel(application: Application):AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    private val productDao = Storage.getInstance(getApplication<Application>().applicationContext).productDao()
    private val db = Firebase.firestore
    private val auth = Firebase.auth

    init {
        viewModelScope.launch {
            Firebase.messaging.token.addOnSuccessListener {
                val title = "Sample com.example.grocify.api.Notification"
                val body = "This is a test notification sent from Kotlin"
                sendFCMNotification(it, title, body)
            }
        }
    }

    private fun sendFCMNotification(token: String, title: String, body: String) {
        val notification = Notification(title, body)
        val message = Message(token, notification)
        val fcmNotification = FCMNotification(message)

        RetrofitObject.fcmService.sendNotification(fcmNotification).enqueue(object : Callback<FCMResponse> {
            override fun onResponse(call: Call<FCMResponse>, response: Response<FCMResponse>) {
                if (response.isSuccessful) {
                    Log.d("FCM", "Notification sent successfully: ${response.body()}")
                } else {
                    Log.e("FCM", "Notification failed: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<FCMResponse>, t: Throwable) {
                Log.e("FCM", "Error sending notification: ${t.message}")
            }
        })
    }

    fun getCurrentInfo(){
        db.collection("users_details")
            .whereEqualTo("uid",auth.currentUser!!.uid)
            .get()
            .addOnSuccessListener { documents ->
                if(documents.isEmpty){
                    _uiState.update {
                        it.copy(
                            resultPaymentMethod = "Nessun metodo di pagamento aggiunto",
                            resultAddress = "Nessun indirizzo aggiunto"
                        )
                    }
                }else{
                    val addresses: List<HashMap<String,Any>> = documents.documents[0].get("addresses") as List<HashMap<String,Any>>
                    val paymentMethods: List<HashMap<String,Any>> = documents.documents[0].get("paymentMethods") as List<HashMap<String,Any>>

                    if(addresses.isEmpty())
                        _uiState.update {
                            it.copy(
                                resultAddress = "Nessun indirizzo aggiunto"
                            )
                        }
                    else {
                        val selectedAddress = addresses.filter { it["selected"] as Boolean }
                        if(selectedAddress.isEmpty())
                            _uiState.update {
                                it.copy(
                                    resultAddress = "Nessun indirizzo selezionato"
                                )
                            }
                        else {
                            val addressClass = Address(
                                name = selectedAddress[0]["name"] as String,
                                address = selectedAddress[0]["address"] as String,
                                city = selectedAddress[0]["city"] as String,
                                civic = selectedAddress[0]["civic"] as String,
                                selected = selectedAddress[0]["selected"] as Boolean
                            )
                            _uiState.update {
                                it.copy(
                                    resultAddress = "",
                                    currentAddress = addressClass
                                )
                            }
                        }
                    }

                    if(paymentMethods.isEmpty())
                        _uiState.update {
                            it.copy(
                                resultPaymentMethod = "Nessun metodo di pagamento aggiunto"
                            )
                        }
                    else {
                        val selectedPaymentMethod = paymentMethods.filter { it["selected"] as Boolean }
                        if(selectedPaymentMethod.isEmpty())
                            _uiState.update {
                                it.copy(
                                    resultPaymentMethod = "Nessun metodo di pagamento selezionato"
                                )
                            }
                        else {
                            val paymentClass = PaymentMethod(
                                owner = selectedPaymentMethod[0]["owner"] as String,
                                number = maskCardNumber(selectedPaymentMethod[0]["number"] as String),
                                expireDate = selectedPaymentMethod[0]["expireDate"] as String,
                                cvc = selectedPaymentMethod[0]["cvc"] as String,
                                selected = selectedPaymentMethod[0]["selected"] as Boolean
                            )
                            _uiState.update {
                                it.copy(
                                    resultPaymentMethod = "",
                                    currentPaymentMethod = paymentClass
                                )
                            }
                        }
                    }
                }
            }
    }


    fun createNewOrder(flagCart: String, totalPrice:Double){
        //prendo ora e data attuali nel formato indicato
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        val localDate = LocalDateTime.now().format(formatter)
        val dateTime = localDate.split(" ")

        //arrotondo il prezzo finale alla seconda cifra decimale
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.DOWN
        val roundedTotalPrice = df.format(totalPrice).replace(',', '.')

        viewModelScope.launch{
            //prendo tutti i prodotti dal carrello locale
            val products = productDao.getProducts(flagCart, auth.currentUser!!.uid).toList()
            val lightProducts: MutableList<HashMap<String,Any>> = mutableListOf()

            for (product in products){
                if(product.threshold == 0){
                    lightProducts.add(
                        hashMapOf(
                            "id" to product.id,
                            "name" to product.name,
                            "units" to product.units,
                            "image" to product.image,
                            "quantity" to product.quantity
                        )
                    )
                }
                else{
                    lightProducts.add(
                        hashMapOf(
                            "id" to product.id,
                            "name" to product.name,
                            "units" to product.units,
                            "image" to product.image,
                            "quantity" to product.quantity,
                            "threshold" to product.threshold.toString()
                        )
                    )
                }
            }

            //creo un nuovo ordine e lo aggiungo al db
            val newOrder = Order(
                cart = lightProducts,
                userId = auth.currentUser!!.uid,
                status = if(flagCart == "online") "in attesa" else "concluso",
                destination = "${_uiState.value.currentAddress.city}, ${_uiState.value.currentAddress.address} ${_uiState.value.currentAddress.civic}",
                totalPrice = roundedTotalPrice.toDouble(),
                type = flagCart,
                date = dateTime[0],
                time = dateTime[1]
            )

            val ordersRef = db.collection("orders")
            ordersRef
                .add(newOrder)
                .addOnSuccessListener { document ->
                    val hash = if (document.id.hashCode() < 0)  document.id.hashCode() else -document.id.hashCode()
                    val orderId = hash.toString().replace("-","#")
                    ordersRef.document(document.id).update(
                        "orderId",orderId
                    )
                    _uiState.update { it.copy(orderId = orderId) }
                }
        }
    }

    fun userHasRunningOrder(){
        viewModelScope.launch {
            //
            db.collection("orders")
                .whereEqualTo("userId", auth.currentUser!!.uid)
                .whereNotEqualTo("status", "concluso")
                .get()
                .addOnSuccessListener { documents ->
                        _uiState.update { it.copy(
                            userHasRunningOrder = !documents.isEmpty
                        ) }
                }
        }
    }
}

