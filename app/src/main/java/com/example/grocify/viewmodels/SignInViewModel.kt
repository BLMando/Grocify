package com.example.grocify.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocify.compose.signIn.GoogleSignInResult
import com.example.grocify.data.signIn.GoogleSignInState
import com.example.grocify.data.signIn.SignInUiState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignInViewModel(application: Application): AndroidViewModel(application) {

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    private val _googleSignInState = MutableStateFlow(GoogleSignInState())
    val googleSignInState: StateFlow<GoogleSignInState> = _googleSignInState.asStateFlow()

    private val _signInState = MutableStateFlow(SignInUiState())
    val signInState: StateFlow<SignInUiState> = _signInState.asStateFlow()

    fun signIn(email: String, password: String) {

        val passwordStatus = verifyPassword(password)
        val emailStatus = verifyEmail(email)

        if (!passwordStatus) {
            _signInState.update { currentState ->
                currentState.copy(
                    passwordError = "Inserisci una password valida (almeno sei caratteri)",
                    isPasswordValid = false
                )
            }
        }else
            _signInState.update { currentState ->
                currentState.copy(
                    passwordError = "",
                    isPasswordValid = true
                )
            }

        if (!emailStatus) {
            _signInState.update { currentState ->
                currentState.copy(
                    emailError = "Inserisci un email valida",
                    isEmailValid = false
                )
            }
        }else
            _signInState.update { currentState ->
                currentState.copy(
                    emailError = "",
                    isEmailValid = true
                )
            }

        if (passwordStatus && emailStatus) {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                _signInState.update { currentState ->
                                    currentState.copy(
                                        isSuccessful = true,
                                    )
                                }
                            } else
                                _signInState.update { currentState ->
                                    currentState.copy(
                                        isSuccessful = false,
                                        signInError = task.exception?.localizedMessage.toString()
                                    )
                                }
                        }
                    resetState()
                }
            }
        }
    }

    fun onSignInResult(result: GoogleSignInResult){
        if(result.data != null){
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    result.data.let {
                        db.collection("users")
                            .whereEqualTo("email", it.email)
                            .get()
                            .addOnSuccessListener { documents ->
                                if (documents.isEmpty)
                                    db.collection("users")
                                        .add(it)
                                        .addOnFailureListener { error ->
                                            _googleSignInState.update { currentState ->
                                                currentState.copy(
                                                    isSignInSuccessful = false,
                                                    signInError = error.localizedMessage
                                                )
                                            }
                                        }
                                _googleSignInState.update { currentState ->
                                    currentState.copy(
                                        isSignInSuccessful = true,
                                        signInError = result.error
                                    )
                                }
                            }
                    }
                }
            }
        }else
            _googleSignInState.update { currentState ->
                currentState.copy(
                    isSignInSuccessful = false,
                    signInError = result.error
                )
            }
    }

    private fun isNotEmpty(value:String) : Boolean = value.isNotEmpty() && value.isNotBlank()

    private fun verifyEmail(email: String): Boolean = isNotEmpty(email) && isEmailValid(email)

    private fun isEmailValid(email: String): Boolean {
        val emailRegex = Regex("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}")
        return emailRegex.matches(email)
    }

    private fun verifyPassword(password: String): Boolean = isNotEmpty(password) && password.length >= 6

    fun resetGoogleState() =  _googleSignInState.update { GoogleSignInState() }
    private fun resetState() = _signInState.update { SignInUiState() }
}