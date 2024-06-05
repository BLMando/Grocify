package com.example.grocify.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocify.data.UserPaymentMethodsUiState
import com.example.grocify.model.PaymentMethod
import com.example.grocify.model.UserDetails
import com.example.grocify.util.dataClassToMap
import com.example.grocify.util.isNotEmpty
import com.example.grocify.util.isValidCreditCardNumber
import com.example.grocify.util.isValidExpireDate
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UserPaymentMethodsViewModel (application: Application): AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(UserPaymentMethodsUiState())
    val uiState: StateFlow<UserPaymentMethodsUiState> = _uiState.asStateFlow()

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    fun addNewPaymentMethod(owner:String, number: String, cvc: String, expireDate:String){

        //INZIO CONTROLLO DELL'INPUT
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
        //FINE CONTROLLO DELL'INPUT

        if(ownerStatus && numberStatus && cvcStatus && expireDateStatus){
            //se tutti i campi sono validi, procedo con l'inserimento

            val userDetailsRef = db.collection("users_details")

            val paymentMethodObject = PaymentMethod(
                owner = owner,
                number = number,
                expireDate = expireDate,
                cvc = cvc.toInt(),
                selected = false
            )
            viewModelScope.launch {
                //accedo al documento dell'utente attualmente loggato
                userDetailsRef
                    .whereEqualTo("uid", auth.currentUser!!.uid)
                    .get()
                    .addOnSuccessListener { document ->
                        if(document.isEmpty){
                            //se l'utente non ha ancora alcun dettaglio di spedizione, lo creo
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
                            //altrimento lo aggiungo
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

    fun getAllPaymentMethods(){

        val paymentMethods = mutableListOf<PaymentMethod>()

        viewModelScope.launch {
            //accedo al documento dell'utente attualmente loggato
            db.collection("users_details")
                .whereEqualTo("uid", auth.currentUser!!.uid)
                .get()
                .addOnSuccessListener { document ->
                    if(!document.isEmpty){
                        //se l'utente ha un documento per i suoi dettagli, vado a leggere i metodi di pagamento
                        val paymentMethodsList: List<HashMap<String, Any>> = document.first().get("paymentMethods") as List<HashMap<String, Any>>
                        if(paymentMethodsList.isNotEmpty()){
                            //se ha inserito metodi di pagamento li leggo
                            paymentMethodsList.listIterator().forEach{ method ->
                                paymentMethods.add(
                                    PaymentMethod(
                                        owner = method["owner"] as String,
                                        number = method["number"] as String,
                                        expireDate = method["expireDate"] as String,
                                        cvc = method["cvc"].toString().toInt(),
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
                            //l'utente non ha inserito nessun metodo di pagamento
                            _uiState.update { currentState ->
                                currentState.copy(
                                    result = "Nessuno metodo di pagamento presente"
                                )
                            }
                        }
                    }else{
                        //l'utente non ha ancora alcun dettaglio di spedizione
                        _uiState.update { currentState ->
                            currentState.copy(
                                result = "Nessuno metodo di pagamento presente"
                            )
                        }
                    }
                }
        }
        //reimposto lo stato dei flag per attivare il LaunchedEffect
        resetFlag()
    }

    fun setPaymentMethodSelected(paymentMethod: PaymentMethod){

        val userDetailsRef = db.collection("users_details")

        viewModelScope.launch {
            //accedo al documento dell'utente attualmente loggato
            userDetailsRef
                .whereEqualTo("uid", auth.currentUser!!.uid)
                .get()
                .addOnSuccessListener { documents ->
                    userDetailsRef.document(documents.first().id)
                        .get()
                        .addOnSuccessListener{ document ->

                            val paymentMethodsList: List<HashMap<String, Any?>> = document.get("paymentMethods") as List<HashMap<String, Any?>>

                            //converto l'oggetto PaymentMethod in un HashMap per poter confrontarlo con gli altri oggetti
                            val paymentMethodMap = dataClassToMap(paymentMethod)

                            //quando viene selezionato un nuovo metodo di pagamento imposto quello precedentemete selezionato a false
                            paymentMethodsList.forEach { method ->
                                if (method["selected"] == true){
                                    method["selected"] = false
                                }
                            }

                            //vado a trovare nella lista dei metodi di pagamento quello che è stato selezionato e lo seleziono
                            paymentMethodsList.forEach { method ->
                                method["cvc"] = method["cvc"].toString().toInt()
                                if(method.entries == paymentMethodMap.entries){
                                    method["selected"] = true
                                }
                            }

                            //aggiorno il documento sul db
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

    fun updatePaymentMethod(paymentMethod: PaymentMethod, ready:Boolean){
        //il flag ready indica se l'utente ha premuto il tasto "modifica"

        if(!ready) {
            //se è false vuoldire che ha cliccato sul tasto "modifica" del menù a tendina e quindi mi salvo il metodo di pagamento da voler modificare
            _uiState.update {
                it.copy(
                    paymentMethodToUpdate = paymentMethod
                )
            }
        }else{
            //quando il flag vale true vuol dire che l'utente ha effettuato le modifiche e quindi viene passatto come "paymentMethod" l'oggetto con i campi aggiornati
            //INZIO CONTROLLO DELL'INPUT
            val ownerStatus = isNotEmpty(paymentMethod.owner)
            val numberStatus = isValidCreditCardNumber(paymentMethod.number)
            val cvcStatus = isNotEmpty(paymentMethod.cvc.toString())
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
            //FINE CONTROLLO DELL'INPUT

            if(ownerStatus && numberStatus && cvcStatus && expireDateStatus) {
                val userDetailsRef = db.collection("users_details")
                userDetailsRef
                    .whereEqualTo("uid", auth.currentUser!!.uid)
                    .get()
                    .addOnSuccessListener { documents ->
                        //accedo al documento dell'utente attualmente loggato
                        userDetailsRef.document(documents.documents[0].id)
                            //rimuovo il vecchio metodo di pagamento dall'array di metodi di pagamento dell'utente
                            .update(
                                "paymentMethods",
                                FieldValue.arrayRemove(_uiState.value.paymentMethodToUpdate)
                            )
                            .addOnSuccessListener {
                                //aggiungo il nuovo metodo di pagamento
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