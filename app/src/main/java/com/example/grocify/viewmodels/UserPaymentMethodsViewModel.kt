package com.example.grocify.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocify.states.UserPaymentMethodsUiState
import com.example.grocify.model.PaymentMethod
import com.example.grocify.model.UserDetails
import com.example.grocify.utils.dataClassToMap
import com.example.grocify.utils.isNotEmpty
import com.example.grocify.utils.isValidCreditCardNumber
import com.example.grocify.utils.isValidExpireDate
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel class for UserPaymentsScreen handling user's payment methods.
 * @param application The application context.
 */
class UserPaymentMethodsViewModel (application: Application): AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(UserPaymentMethodsUiState())
    val uiState: StateFlow<UserPaymentMethodsUiState> = _uiState.asStateFlow()

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    /**
     * Function to add a new payment method to the user's payment methods list
     * performs input validations and then adds the new payment method to the list
     * and update the user's details in the Firestore database.
     * @param owner The name of the payment method owner
     * @param number The credit card number
     * @param cvc The CVC code of the credit card
     * @param expireDate The expire date of the credit card
     */
    fun addNewPaymentMethod(owner:String, number: String, cvc: String, expireDate:String){

        val ownerStatus = isNotEmpty(owner)
        val numberStatus = isValidCreditCardNumber(number)
        val cvcStatus = isNotEmpty(cvc)
        val expireDateStatus = isValidExpireDate(expireDate)

        if(!ownerStatus){
            _uiState.update {
                it.copy(
                    ownerError = "Inserire il nome e cognome dell'intestatario",
                    isOwnerValid = false
                )
            }
        }else{
            _uiState.update {
                it.copy(
                    ownerError = "",
                    isOwnerValid = true
                )
            }
        }

        if(!numberStatus){
            _uiState.update {
                it.copy(
                    numberError = "Inserire un numero di carta valido",
                    isNumberValid = false
                )
            }
        }else{
            _uiState.update {
                it.copy(
                    numberError = "",
                    isNumberValid = true
                )
            }
        }

        if(!cvcStatus){
            _uiState.update {
                it.copy(
                    cvcError = "Inserire il codice CVC",
                    isCvcValid = false
                )
            }
        }else{
            _uiState.update {
                it.copy(
                    cvcError = "",
                    isCvcValid = true
                )
            }
        }

        if(!expireDateStatus){
            _uiState.update {
                it.copy(
                    expireDateError = "Inserire una data di scadenza valida",
                    isExpireDateValid = false
                )
            }
        }else{
            _uiState.update {
                it.copy(
                    expireDateError = "",
                    isExpireDateValid = true
                )
            }
        }

        if(ownerStatus && numberStatus && cvcStatus && expireDateStatus){

            val userDetailsRef = db.collection("users_details")

            val paymentMethodObject = PaymentMethod(
                owner = owner,
                number = number,
                expireDate = expireDate,
                cvc = cvc,
                selected = false
            )
            viewModelScope.launch {
                userDetailsRef
                    .whereEqualTo("uid", auth.currentUser!!.uid)
                    .get()
                    .addOnSuccessListener { document ->
                        if(document.isEmpty){
                            userDetailsRef.add(
                                UserDetails(
                                    uid = auth.currentUser!!.uid,
                                    paymentMethods = mutableListOf(paymentMethodObject)
                                )
                            ).addOnSuccessListener {
                                _uiState.update {
                                    it.copy(
                                        isInsertSuccessful = true
                                    )
                                }
                            }
                        }else{
                            userDetailsRef.document(document.first().id)
                                .update("paymentMethods", FieldValue.arrayUnion(paymentMethodObject))
                                .addOnSuccessListener {
                                    _uiState.update {
                                        it.copy(
                                            isInsertSuccessful = true
                                        )
                                    }
                                }
                        }
                    }
            }

        }
    }

    /**
     * Function to get all the payment methods of the user from the Firestore database
     */
    fun getAllPaymentMethods(){

        val paymentMethods = mutableListOf<PaymentMethod>()

        viewModelScope.launch {
            db.collection("users_details")
                .whereEqualTo("uid", auth.currentUser!!.uid)
                .get()
                .addOnSuccessListener { document ->
                    if(!document.isEmpty){
                        val paymentMethodsList: List<HashMap<String, Any>> = document.first().get("paymentMethods") as List<HashMap<String, Any>>
                        if(paymentMethodsList.isNotEmpty()){
                            paymentMethodsList.listIterator().forEach{ method ->
                                paymentMethods.add(
                                    PaymentMethod(
                                        owner = method["owner"] as String,
                                        number = method["number"] as String,
                                        expireDate = method["expireDate"] as String,
                                        cvc = method["cvc"] as String,
                                        selected = method["selected"] as Boolean
                                    )
                                )
                            }
                            _uiState.update { currentState ->
                                currentState.copy(
                                    paymentMethods = paymentMethods,
                                    result = ""
                                )
                            }
                        }else{
                            _uiState.update { currentState ->
                                currentState.copy(
                                    result = "Nessuno metodo di pagamento presente"
                                )
                            }
                        }
                    }else{
                        _uiState.update { currentState ->
                            currentState.copy(
                                result = "Nessuno metodo di pagamento presente"
                            )
                        }
                    }
                }
        }
        resetFlag()
    }

    /**
     * Function to set a selected payment method for the user
     * and update the user's details in Firestore
     * @param paymentMethod The payment method to be set as selected
     */
    fun setPaymentMethodSelected(paymentMethod: PaymentMethod){

        val userDetailsRef = db.collection("users_details")

        viewModelScope.launch {
            userDetailsRef
                .whereEqualTo("uid", auth.currentUser!!.uid)
                .get()
                .addOnSuccessListener { documents ->
                    userDetailsRef.document(documents.first().id)
                        .get()
                        .addOnSuccessListener{ document ->
                            // Get the payment methods list from the document and set to true the selected payment method and false the others
                            val paymentMethodsList: List<HashMap<String, Any?>> = document.get("paymentMethods") as List<HashMap<String, Any?>>

                            val paymentMethodMap = dataClassToMap(paymentMethod)

                            paymentMethodsList.forEach { method ->
                                if (method["selected"] == true){
                                    method["selected"] = false
                                }
                            }

                            paymentMethodsList.forEach { method ->
                                if(method.entries == paymentMethodMap.entries){
                                    method["selected"] = true
                                }
                            }

                            userDetailsRef.document(documents.first().id)
                                .update("paymentMethods", paymentMethodsList)
                                .addOnSuccessListener {
                                    _uiState.update {
                                        it.copy(
                                            isUDSuccessful = true
                                        )
                                    }
                                }
                        }
                }
        }
    }

    /**
     * Function to update a payment method from the user's payment methods list
     * performs input validation on the payment method fields and
     * and update the user's details in Firestore
     * @param paymentMethod The payment method to be updated
     * @param ready A flag indicating if the update is ready to be performed
     */
    fun updatePaymentMethod(paymentMethod: PaymentMethod, ready:Boolean){
        if(!ready) {
            _uiState.update {
                it.copy(
                    paymentMethodToUpdate = paymentMethod
                )
            }
        }else{
            val ownerStatus = isNotEmpty(paymentMethod.owner)
            val numberStatus = isValidCreditCardNumber(paymentMethod.number)
            val cvcStatus = isNotEmpty(paymentMethod.cvc)
            val expireDateStatus = isValidExpireDate(paymentMethod.expireDate)

            if(!ownerStatus){
                _uiState.update {
                    it.copy(
                        ownerError = "Inserire il nome e cognome dell'intestatario",
                        isOwnerValid = false
                    )
                }
            }else{
                _uiState.update {
                    it.copy(
                        ownerError = "",
                        isOwnerValid = true
                    )
                }
            }

            if(!numberStatus){
                _uiState.update {
                    it.copy(
                        numberError = "Inserire un numero di carta valido",
                        isNumberValid = false
                    )
                }
            }else{
                _uiState.update {
                    it.copy(
                        numberError = "",
                        isNumberValid = true
                    )
                }
            }

            if(!cvcStatus){
                _uiState.update {
                    it.copy(
                        cvcError = "Inserire il codice CVC",
                        isCvcValid = false
                    )
                }
            }else{
                _uiState.update {
                    it.copy(
                        cvcError = "",
                        isCvcValid = true
                    )
                }
            }

            if(!expireDateStatus){
                _uiState.update {
                    it.copy(
                        expireDateError = "Inserire una data di scadenza valida",
                        isExpireDateValid = false
                    )
                }
            }else{
                _uiState.update {
                    it.copy(
                        expireDateError = "",
                        isExpireDateValid = true
                    )
                }
            }

            if(ownerStatus && numberStatus && cvcStatus && expireDateStatus) {
                val userDetailsRef = db.collection("users_details")
                userDetailsRef
                    .whereEqualTo("uid", auth.currentUser!!.uid)
                    .get()
                    .addOnSuccessListener { documents ->
                        userDetailsRef.document(documents.documents[0].id)
                            .update(
                                "paymentMethods",
                                FieldValue.arrayRemove(_uiState.value.paymentMethodToUpdate)
                            )
                            .addOnSuccessListener {
                                userDetailsRef.document(documents.documents[0].id)
                                    .update("paymentMethods", FieldValue.arrayUnion(paymentMethod))
                                    .addOnSuccessListener {
                                        _uiState.update {
                                            it.copy(
                                                isInsertSuccessful = true,
                                                paymentMethodToUpdate = null
                                            )
                                        }
                                    }

                            }

                    }
            }
        }
    }

    /**
     * Function to delete an payment method from the user's payment methods list
     * and update the user's details in Firestore
     * @param paymentMethod The payment method to be deleted
     */
    fun deletePaymentMethod(paymentMethod: PaymentMethod){
        val userDetailsRef = db.collection("users_details")

        viewModelScope.launch {
            //accedo al documento dell'utente attualmente loggato
            userDetailsRef
                .whereEqualTo("uid", auth.currentUser!!.uid)
                .get()
                .addOnSuccessListener { documents ->
                    //rimuovo il metodo di pagamento selezionato dall'array di metodi di pagamento dell'utente
                    userDetailsRef.document(documents.first().id)
                        .update("paymentMethods", FieldValue.arrayRemove(paymentMethod))
                        .addOnSuccessListener {
                            _uiState.update {
                                it.copy(
                                    isUDSuccessful = true,
                                    paymentMethodToUpdate = null
                                )
                            }
                        }
                }
        }
    }

    fun setFABClicked(value: Boolean) = run {
        _uiState.update { currentState ->
            currentState.copy(
                isFABClicked = value
            )
        }
    }

    private fun resetFlag() = run { _uiState.update { it.copy(
        isUDSuccessful = false,
        isInsertSuccessful = false
    ) } }

    fun resetFABField() = run { _uiState.update { it.copy(
        paymentMethodToUpdate =  null
    ) } }

}