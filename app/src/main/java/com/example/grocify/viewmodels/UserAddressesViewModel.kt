
package com.example.grocify.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocify.BuildConfig
import com.example.grocify.data.remote.RetrofitObject
import com.example.grocify.states.UserAddressesUiState
import com.example.grocify.model.Address
import com.example.grocify.model.UserDetails
import com.example.grocify.utils.dataClassToMap
import com.example.grocify.utils.isNotEmpty
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tomtom.sdk.location.GeoPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

/**
 * ViewModel class for UserAddressesScreen handling user's addresses.
 * @param application The application context.
 */
class UserAddressesViewModel(application: Application):AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(UserAddressesUiState())
    val uiState: StateFlow<UserAddressesUiState> = _uiState.asStateFlow()

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    private val apiKey = BuildConfig.TOMTOM_API_KEY

    /**
     * Function to add a new address to the user's addresses list
     * and update the user's details in Firestore.
     * @param addressName The name of the address
     * @param address The address string
     * @param city The city of the address
     * @param civic The civic number of the address
     */
    suspend fun addNewAddress(addressName:String, address: String, city:String, civic: String){

        val addressNameStatus = isNotEmpty(addressName)
        val addressStatus = isNotEmpty(address)
        val civicStatus = isNotEmpty(civic)
        val cityStatus = isNotEmpty(city)
        var isValidAddress = true

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
                    addressError = "Inserisci un indirizzo valido",
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

        if(!cityStatus){
            _uiState.update {
                it.copy(
                    cityError = "Inserisci una città",
                    isCityValid = false
                )
            }
        }else{
            _uiState.update {
                it.copy(
                    cityError = "",
                    isCityValid = true
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

        if(addressStatus && civicStatus && cityStatus){
            isValidAddress = checkValidAddress("$city, $address $civic")
            if(!isValidAddress)
                _uiState.update {
                    it.copy(
                        isValidAddressFormat = false,
                        error = "L'indirizzo $city, $address $civic non è valido",
                        isCivicValid = false,
                        isCityValid = false,
                        isAddressValid = false
                    )
                }
        }

        if(addressNameStatus && addressStatus && civicStatus && cityStatus && isValidAddress) {

            val userDetailsRef = db.collection("users_details")

            val addressObject = Address(
                name = addressName,
                address = address,
                city = city,
                civic = civic,
                selected = false
            )

            viewModelScope.launch {
                userDetailsRef
                    .whereEqualTo("uid", auth.currentUser!!.uid)
                    .get()
                    .addOnSuccessListener { document ->
                        if (document.isEmpty) {
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

    private suspend fun checkValidAddress(address: String): Boolean  = withContext(Dispatchers.Main) {
        val deferred = viewModelScope.async(Dispatchers.IO) {
            try {
                val response = RetrofitObject.geocodingService.getUserLocation(address, apiKey)
                response.body()!!.results.isNotEmpty() && response.body()!!.results[0].matchConfidence.score.toInt() == 1
            } catch (e: IOException) {
                Log.d("MapViewModel", "No internet connection")
                null
            } catch (e: HttpException) {
                Log.d("MapViewModel", "Unexpected response")
                null
            }
        }
        deferred.await() == true
    }

    /**
     * Function to retrieve all addresses of the user from Firestore
     * and update the UI state accordingly.
     */
     fun getAllAddresses(){

        val addresses = mutableListOf<Address>()

        viewModelScope.launch {
            db.collection("users_details")
                .whereEqualTo("uid", auth.currentUser!!.uid)
                .get()
                .addOnSuccessListener { document ->
                    if(!document.isEmpty){
                        val addressesList: List<HashMap<String, Any>> = document.first().get("addresses") as List<HashMap<String, Any>>
                        if(addressesList.isNotEmpty()){
                            addressesList.listIterator().forEach{ address ->
                                addresses.add(
                                    Address(
                                        name = address["name"] as String,
                                        address = address["address"] as String,
                                        city = address["city"] as String,
                                        civic = address["civic"] as String,
                                        selected = address["selected"] as Boolean
                                    )
                                )
                            }
                            _uiState.update { currentState ->
                                currentState.copy(
                                    addresses = addresses,
                                    result = ""
                                )
                            }
                        }else{
                            _uiState.update { currentState ->
                                currentState.copy(
                                    result = "Nessuno indirizzo presente"
                                )
                            }
                        }
                    }else{
                        _uiState.update { currentState ->
                            currentState.copy(
                                result = "Nessuno indirizzo presente"
                            )
                        }
                    }
                }
        }
        resetFlag()
    }

    /**
     * Function to set a selected address for the user
     * and update the user's details in Firestore
     * @param address The address to be set as selected
     */
    fun setAddressSelected(address: Address){

        val userDetailsRef = db.collection("users_details")

        viewModelScope.launch {
            userDetailsRef
                .whereEqualTo("uid", auth.currentUser!!.uid)
                .get()
                .addOnSuccessListener { documents ->
                    userDetailsRef.document(documents.first().id)
                        .get()
                        .addOnSuccessListener{ document ->
                            // Get the addresses list from the document and set to true the selected address and false the others
                            val addressesList: List<HashMap<String, Any?>> = document.get("addresses") as List<HashMap<String, Any?>>

                            val addressMap = dataClassToMap(address)

                            addressesList.forEach { addressItem ->
                                if (addressItem["selected"] == true){
                                    addressItem["selected"] = false
                                }
                            }

                            addressesList.forEach { addressItem ->
                                if(addressItem.entries == addressMap.entries){
                                    addressItem["selected"] = true
                                }
                            }

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

    /**
     * Function to update an address from the user's addresses list
     * performs input validation on the address fields and
     * and update the user's details in Firestore
     * @param address The address to be updated
     * @param ready A flag indicating if the update is ready to be performed
     */
    fun updateAddress(address:Address, ready:Boolean){

        if(!ready) {
            _uiState.update {
                it.copy(
                    addressToUpdate = address
                )
            }
        }else{

            val addressNameStatus = isNotEmpty(address.name)
            val addressStatus = isNotEmpty(address.address)
            val cityStatus = isNotEmpty(address.city)
            val civicStatus = isNotEmpty(address.civic)

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

            if(!cityStatus){
                _uiState.update {
                    it.copy(
                        cityError = "Inserisci una città",
                        isCityValid = false
                    )
                }
            }else{
                _uiState.update {
                    it.copy(
                        cityError = "",
                        isCityValid = true
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

            if(addressNameStatus && addressStatus && civicStatus && cityStatus) {
                val userDetailsRef = db.collection("users_details")
                userDetailsRef
                    .whereEqualTo("uid", auth.currentUser!!.uid)
                    .get()
                    .addOnSuccessListener { documents ->
                        userDetailsRef.document(documents.documents[0].id)
                            .update(
                                "addresses",
                                FieldValue.arrayRemove(_uiState.value.addressToUpdate)
                            )
                            .addOnSuccessListener {
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

    /**
     * Function to delete an address from the user's addresses list
     * and update the user's details in Firestore
     * @param address The address to be deleted
     */
    fun deleteAddress(address: Address){
        val userDetailsRef = db.collection("users_details")

        viewModelScope.launch {
            userDetailsRef
                .whereEqualTo("uid", auth.currentUser!!.uid)
                .get()
                .addOnSuccessListener { documents ->
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

    private fun resetFlag() = run { _uiState.update { it.copy(
        isUDSuccessful = false,
        isInsertSuccessful = false
    ) } }

    fun resetFABField() = run { _uiState.update { it.copy(
        addressToUpdate = null
    ) } }

}
