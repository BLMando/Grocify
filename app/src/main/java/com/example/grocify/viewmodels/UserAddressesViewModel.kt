
package com.example.grocify.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocify.data.UserAddressesUiState
import com.example.grocify.model.Address
import com.example.grocify.model.UserDetails
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UserAddressesViewModel(application: Application):AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(UserAddressesUiState())
    val uiState: StateFlow<UserAddressesUiState> = _uiState.asStateFlow()

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    fun addNewAddress(addressName:String, address: String, civic: String){

        //CONTROLLO DELL'INPUT
        val addressNameStatus = isNotEmpty(addressName)
        val addressStatus = isNotEmpty(address)
        val civicStatus = isNotEmpty(civic)

        if(!addressNameStatus){
            _uiState.update {
                it.copy(
                    addressNameError = "Inserisci un nome per l'indirizzo",
                    isAddressNameValid = false
                )
            }
        }else{
            _uiState.update {
                it.copy(
                    addressNameError = "",
                    isAddressNameValid = true
                )
            }
        }

        if(!addressStatus){
            _uiState.update {
                it.copy(
                    addressError = "Inserisci un indirizzo",
                    isAddressValid = false
                )
            }
        }else{
            _uiState.update {
                it.copy(
                    addressError = "",
                    isAddressValid = true
                )
            }
        }

        if(!civicStatus){
            _uiState.update {
                it.copy(
                    civicError = "Inserisci un civico",
                    isCivicValid = false
                )
            }
        }else{
            _uiState.update {
                it.copy(
                    civicError = "",
                    isCivicValid = true
                )
            }
        }
        //FINE CONTROLLO DELL'INPUT

        if(addressNameStatus && addressStatus && civicStatus) {
            //se tutti i campi sono validi, procedo con l'inserimento

            val userDetailsRef = db.collection("users_details")

            val addressObject = Address(
                name = addressName,
                address = address,
                civic = civic.toInt(),
                selected = false
            )

            viewModelScope.launch {
                //accedo al documento dell'utente attualmente loggato
                userDetailsRef
                    .whereEqualTo("uid", auth.currentUser!!.uid)
                    .get()
                    .addOnSuccessListener { document ->
                        if (document.isEmpty) {
                            //se l'utente non ha ancora alcun dettaglio di spedizione, lo creo
                            userDetailsRef.add(
                                UserDetails(
                                    uid = auth.currentUser!!.uid,
                                    addresses = mutableListOf(addressObject)
                                )
                            ).addOnSuccessListener {
                                _uiState.update {
                                    it.copy(
                                        isInsertSuccessful = true
                                    )
                                }
                            }
                        } else {
                            //altrimento lo aggiungo
                            userDetailsRef.document(document.first().id)
                                .update("addresses", FieldValue.arrayUnion(addressObject))
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

     fun getAllAddresses(){

        val addresses = mutableListOf<Address>()

        viewModelScope.launch {
            //accedo al documento dell'utente attualmente loggato
            db.collection("users_details")
                .whereEqualTo("uid", auth.currentUser!!.uid)
                .get()
                .addOnSuccessListener { document ->
                    if(!document.isEmpty){
                        //se l'utente ha un documento per i suoi dettagli, vado a leggere gli indirizzi
                        val addressesList: List<HashMap<String, Any>> = document.first().get("addresses") as List<HashMap<String, Any>>
                        if(addressesList.isNotEmpty()){
                            //se ha inserito indirizzi li leggo
                            addressesList.listIterator().forEach{ address ->
                                addresses.add(
                                    Address(
                                        name = address["name"] as String,
                                        address = address["address"] as String,
                                        civic = (address["civic"] as Long).toInt(),
                                        selected = address["selected"] as Boolean
                                    )
                                )
                            }
                            _uiState.update { currentState ->
                                currentState.copy(
                                    addresses = addresses,
                                    result = null
                                )
                            }
                        }else{
                            //l'utente non ha inserito nessun indirizzo
                            _uiState.update { currentState ->
                                currentState.copy(
                                    result = "Nessuno indirizzo presente"
                                )
                            }
                        }
                    }else{
                        //l'utente non ha ancora alcun dettaglio di spedizione
                        _uiState.update { currentState ->
                            currentState.copy(
                                result = "Nessuno indirizzo presente"
                            )
                        }
                    }
                }
        }
         //reimposto lo stato dei flag per attivare il LaunchedEffect
        resetFlag()
    }

    fun setAddressSelected(address: Address){

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

                            val addressesList: List<HashMap<String, Any?>> = document.get("addresses") as List<HashMap<String, Any?>>

                            //converto l'oggetto Address in un HashMap per poter confrontarlo con gli altri oggetti
                            val addressMap = dataClassToMap(address)

                            //quando viene selezionato un nuovo indirizzo imposto quello precedentemete selezionato a false
                            addressesList.forEach { addressItem ->
                                if (addressItem["selected"] == true){
                                    addressItem["selected"] = false
                                }
                            }

                            //vado a trovare nella lista degli indirizzi quello che è stato selezionato e lo seleziono
                            addressesList.forEach { addressItem ->
                                addressItem["civic"] = (addressItem["civic"] as Long).toInt()
                                if(addressItem.entries == addressMap.entries){
                                    addressItem["selected"] = true
                                }
                            }

                            //aggiorno il documento sul db
                            userDetailsRef.document(documents.first().id)
                                .update("addresses", addressesList)
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

    fun updateAddress(address:Address, ready:Boolean){
        //il flag ready indica se l'utente ha premuto il tasto "modifica"

        if(!ready) {
            //se è false vuoldire che ha cliccato sul tasto "modifica" del menù a tendina e quindi mi salvo l'indirizzo da voler modificare
            _uiState.update {
                it.copy(
                    addressToUpdate = address
                )
            }
        }else{
            //quando il flag vale true vuol dire che l'utente ha effettuato le modifiche e quindi viene passatto come "address" l'oggetto con i campi aggiornati
            //CONTROLLO DELL'INPUT
            val addressNameStatus = isNotEmpty(address.name)
            val addressStatus = isNotEmpty(address.address)
            val civicStatus = isNotEmpty(address.civic.toString())

            if(!addressNameStatus){
                _uiState.update {
                    it.copy(
                        addressNameError = "Inserisci un nome per l'indirizzo",
                        isAddressNameValid = false
                    )
                }
            }else{
                _uiState.update {
                    it.copy(
                        addressNameError = "",
                        isAddressNameValid = true
                    )
                }
            }

            if(!addressStatus){
                _uiState.update {
                    it.copy(
                        addressError = "Inserisci un indirizzo",
                        isAddressValid = false
                    )
                }
            }else{
                _uiState.update {
                    it.copy(
                        addressError = "",
                        isAddressValid = true
                    )
                }
            }

            if(!civicStatus){
                _uiState.update {
                    it.copy(
                        civicError = "Inserisci un civico",
                        isCivicValid = false
                    )
                }
            }else{
                _uiState.update {
                    it.copy(
                        civicError = "",
                        isCivicValid = true
                    )
                }
            }
            //FINE CONTROLLO DELL'INPUT

            if(addressNameStatus && addressStatus && civicStatus) {
                val userDetailsRef = db.collection("users_details")
                userDetailsRef
                    .whereEqualTo("uid", auth.currentUser!!.uid)
                    .get()
                    .addOnSuccessListener { documents ->
                        //accedo al documento dell'utente attualmente loggato
                        userDetailsRef.document(documents.documents[0].id)
                            //rimuovo il vecchio indirizzo dall'array di indirizzi dell'utente
                            .update(
                                "addresses",
                                FieldValue.arrayRemove(_uiState.value.addressToUpdate)
                            )
                            .addOnSuccessListener {
                                //aggiungo il nuovo indirizzo
                                userDetailsRef.document(documents.documents[0].id)
                                    .update("addresses", FieldValue.arrayUnion(address))
                                    .addOnSuccessListener {
                                        _uiState.update {
                                            it.copy(
                                                isInsertSuccessful = true,
                                                addressToUpdate = null
                                            )
                                        }
                                    }

                            }

                    }
            }
        }
    }

    fun deleteAddress(address: Address){
        val userDetailsRef = db.collection("users_details")

        viewModelScope.launch {
            //accedo al documento dell'utente attualmente loggato
            userDetailsRef
                .whereEqualTo("uid", auth.currentUser!!.uid)
                .get()
                .addOnSuccessListener { documents ->
                    //rimuovo l'indirizzo selezionato dall'array di indirizzi dell'utente
                    userDetailsRef.document(documents.first().id)
                        .update("addresses", FieldValue.arrayRemove(address))
                        .addOnSuccessListener {
                            _uiState.update {
                                it.copy(
                                    isUDSuccessful = true,
                                    addressToUpdate = null
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

    private fun isNotEmpty(value:String) : Boolean = value.isNotEmpty() && value.isNotBlank()

    private fun resetFlag() = run { _uiState.update { it.copy(
        isUDSuccessful = false,
        isInsertSuccessful = false
    ) } }

    fun resetFABField() = run { _uiState.update { it.copy(
        addressToUpdate = null
    ) } }


    private fun dataClassToMap(data: Address): HashMap<String, Any?> {
        val map = hashMapOf<String, Any?>()
        data::class.members
            .filterIsInstance<kotlin.reflect.KProperty<*>>()
            .forEach { property ->
                map[property.name] = property.getter.call(data)
            }
        return map
    }
}
