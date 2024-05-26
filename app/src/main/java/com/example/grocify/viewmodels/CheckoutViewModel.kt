package com.example.grocify.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.grocify.data.CheckoutUiState
import com.example.grocify.model.Address
import com.example.grocify.model.PaymentMethod
import com.example.grocify.util.maskCardNumber
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CheckoutViewModel(application: Application):AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    fun getCurrentInfo(){
        db.collection("users_details")
            .whereEqualTo("uid",auth.currentUser!!.uid)
            .get()
            .addOnSuccessListener { documents ->
                if(documents.isEmpty){
                    _uiState.update {
                        it.copy(
                            result = "Nessuna opzione di spedizione specificata"
                        )
                    }
                }else{
                    val addresses: List<HashMap<String,Any>> = documents.documents[0].get("addresses") as List<HashMap<String,Any>>
                    val paymentMethods: List<HashMap<String,Any>> = documents.documents[0].get("paymentMethods") as List<HashMap<String,Any>>
                    Log.d("CheckoutViewModel", "$addresses $paymentMethods")

                    if(addresses.isEmpty())
                        _uiState.update {
                            it.copy(
                                result = "Nessun indirizzo aggiunto"
                            )
                        }
                    else {
                        val selectedAddress = addresses.filter { it["selected"] as Boolean }

                        if(selectedAddress.isEmpty())
                            _uiState.update {
                                it.copy(
                                    result = "Nessun indirizzo selezionato"
                                )
                            }
                        else {
                            val addressClass = Address(
                                name = selectedAddress[0]["name"] as String,
                                address = selectedAddress[0]["address"] as String,
                                civic = (selectedAddress[0]["civic"] as Long).toInt(),
                                selected = selectedAddress[0]["selected"] as Boolean
                            )
                            _uiState.update {
                                it.copy(
                                    currentAddress = addressClass
                                )
                            }
                        }
                    }

                    if(paymentMethods.isEmpty())
                        _uiState.update {
                            it.copy(
                                result = "Nessun metodo di pagamento aggiunto"
                            )
                        }
                    else {
                        val selectedPaymentMethod = paymentMethods.filter { it["selected"] as Boolean }
                        if(selectedPaymentMethod.isEmpty())
                            _uiState.update {
                                it.copy(
                                    result = "Nessun metodo di pagamento selezionato"
                                )
                            }
                        else {
                            val paymentClass = PaymentMethod(
                                owner = selectedPaymentMethod[0]["owner"] as String,
                                number = maskCardNumber(selectedPaymentMethod[0]["number"] as String),
                                expireDate = selectedPaymentMethod[0]["expireDate"] as String,
                                cvc = (selectedPaymentMethod[0]["cvc"] as Long).toInt(),
                                selected = selectedPaymentMethod[0]["selected"] as Boolean
                            )
                            _uiState.update {
                                it.copy(
                                    currentPaymentMethod = paymentClass
                                )
                            }
                        }
                    }
                }
            }
    }
}

