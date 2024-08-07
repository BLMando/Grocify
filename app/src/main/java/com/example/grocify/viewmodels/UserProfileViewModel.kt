package com.example.grocify.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocify.states.UserProfileUiState
import com.example.grocify.model.User
import com.example.grocify.utils.isNotEmpty
import com.example.grocify.utils.verifyPassword
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel class for UserProfileScreen handling user's personal data.
 * @param application The application context.
 */
class UserProfileViewModel(application: Application): AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(UserProfileUiState())
    val uiState: StateFlow<UserProfileUiState> = _uiState.asStateFlow()

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    /**
     * Method to retrieve the user's profile data from Firestore.
     */
    fun getUserProfile() {
        viewModelScope.launch {
            val user = auth.currentUser
            if (user != null) {
                //se ho un utente loggato prelevo le sue informazioni
                db.collection("users")
                    .whereEqualTo("uid", user.uid)
                    .get()
                    .addOnSuccessListener { document ->
                            //aggiorno lo stato dell'applicazione
                            _uiState.update { currentState ->
                                currentState.copy(
                                    user = User(
                                        uid = document.documents[0].get("uid").toString(),
                                        name = document.documents[0].get("name").toString().replaceFirstChar { it.uppercase() },
                                        surname = document.documents[0].get("surname").toString().replaceFirstChar { it.uppercase() },
                                        email = document.documents[0].get("email").toString(),
                                        password = document.documents[0].get("password").toString(),
                                        profilePic = document.documents[0].get("profilePic").toString(),
                                        role = document.documents[0].get("role").toString(),
                                    )
                                )
                            }
                    }
            }
        }
    }

    /**
     * Method to update the user's profile data in Firestore and Firebase Authentication
     * performs user data validation and use reauthenticate method to verify the user's current password
     * before updating the password.
     * @param user The user object containing the updated data.
     */
    fun updateUserProfile(user: User) {

        Log.e("UserOptionsViewModel", "updateUserProfile: $user")

        //controllo i dati inseriti dall'utente
        val nameStatus = isNotEmpty(user.name.toString())
        val surnameStatus = isNotEmpty(user.surname.toString())
        val passwordStatus = verifyPassword(user.password.toString())

        //aggiorno lo stato dell'applicazione in base ai risultati dei controlli
        if(!nameStatus){
            _uiState.update { currentState ->
                currentState.copy(
                    nameError = "Il nome non può essere vuoto",
                    isNameValid = false
                )
            }
        }else{
            _uiState.update { currentState ->
                currentState.copy(
                    nameError = "",
                    isNameValid = true
                )
            }
        }

        if (!surnameStatus){
            _uiState.update { currentState ->
                currentState.copy(
                    surnameError = "Il cognome non può essere vuoto",
                    isSurnameValid = false
                )
            }
        }else
            _uiState.update { currentState ->
                currentState.copy(
                    surnameError = "",
                    isSurnameValid = true
                )
            }

        if(!passwordStatus){
            _uiState.update { currentState ->
                currentState.copy(
                    passwordError = "Inserisci una password valida (almeno sei caratteri)",
                    isPasswordValid = false
                )
            }
        }else{
            _uiState.update { currentState ->
                currentState.copy(
                    passwordError = "",
                    isPasswordValid = true
                )
            }
        }

        if(nameStatus && surnameStatus && passwordStatus){
            viewModelScope.launch {
                val userDb = db.collection("users")
                userDb
                    .whereEqualTo("uid", user.uid)
                    .get()
                    .addOnSuccessListener { document ->
                        // since changing passwords is a sensitive operation, firebase requires you to authenticate again before proceeding
                        val credential = EmailAuthProvider.getCredential(auth.currentUser?.email.toString(), document.documents[0].get("password").toString())
                        auth.currentUser!!.reauthenticate(credential)
                            .addOnCompleteListener { taskReauth ->
                                if(taskReauth.isSuccessful){
                                    auth.currentUser!!.updatePassword(user.password.toString())
                                        .addOnCompleteListener { taskUpdatePsw ->
                                            if (taskUpdatePsw.isSuccessful) {
                                                userDb.document(document.documents[0].id).update(
                                                    "name",
                                                    user.name,
                                                    "surname",
                                                    user.surname,
                                                    "password",
                                                    user.password
                                                ).addOnCompleteListener { taskDB ->
                                                    if (taskDB.isSuccessful) {
                                                        _uiState.update { currentState ->
                                                            currentState.copy(
                                                                isSuccessful = true,
                                                                user = User(
                                                                    uid = user.uid.toString(),
                                                                    name = user.name.toString(),
                                                                    surname = user.surname.toString(),
                                                                    email = user.email.toString(),
                                                                    password = user.password.toString(),
                                                                    profilePic = user.profilePic,
                                                                    role = user.role,
                                                                )
                                                            )
                                                        }
                                                    } else {
                                                        _uiState.update { currentState ->
                                                            currentState.copy(
                                                                error = "Errore durante l'aggiornamento dei dati"
                                                            )
                                                        }
                                                    }
                                                }
                                            } else {
                                                _uiState.update { currentState ->
                                                    currentState.copy(
                                                        error = "Errore durante l'aggiornamento della password"
                                                    )
                                                }
                                            }
                                        }
                                }else {
                                    _uiState.update { currentState ->
                                        currentState.copy(
                                            error = "Errore di autenticazione, per procedere effettuare di nuovo il login"
                                        )
                                    }
                                }
                            }
                    }
                resetState()
            }
        }
    }

    private fun resetState() = _uiState.update { UserProfileUiState() }
}