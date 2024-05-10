package com.example.grocify.viewmodels

import android.app.Application
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocify.compose.GoogleSignInResult
import com.example.grocify.data.GoogleSignInState
import com.example.grocify.data.SignInUiState
import com.example.grocify.data.SignUpUiState
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

class SignInViewModel(application: Application): AndroidViewModel(application){

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    private val _googleSignInState = MutableStateFlow(GoogleSignInState())
    val googleSignInState: StateFlow<GoogleSignInState> = _googleSignInState.asStateFlow()

    private val _signInState = MutableStateFlow(SignInUiState())
    val signInState: StateFlow<SignInUiState> = _signInState.asStateFlow()

    fun signIn(email:String, password:String){

        verifyPassword(password)
        verifyEmail(email)

        if(_signInState.value.isEmailValid && _signInState.value.isPasswordValid){
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    auth.signInWithEmailAndPassword(email,password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                _signInState.update { currentState ->
                                    currentState.copy(
                                        isSuccessful = true,
                                        signInError = ""
                                    )
                                }
                            }else
                                _signInState.update { currentState ->
                                    currentState.copy(
                                        isSuccessful = false,
                                        signInError = task.exception.toString()
                                    )
                                }
                        }
                }
            }
        }
    }

    fun onSignInResult(result: GoogleSignInResult){
        if(result.data != null){
            result.data.let {
                db.collection("users")
                    .add(it)
                    .addOnSuccessListener {
                        _googleSignInState.update { currentState ->
                            currentState.copy(
                                isSignInSuccessful = true,
                                signInError = result.error
                            )
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

    private fun verifyEmail(email: String){

        var error = true

        if(isEmpty(email)){
            _signInState.update { currentState ->
                currentState.copy(
                    emailError = "L'email non può essere vuota",
                    isEmailValid = false
                )
            }
        }else
            error = false

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            _signInState.update { currentState ->
                currentState.copy(
                    emailError = "L'email ha un formato non valido",
                    isEmailValid = false
                )
            }
        }else
            error = false

        if(!error)
            _signInState.update { currentState ->
                currentState.copy(
                    emailError = "",
                    isEmailValid = true
                )
            }
    }

    private fun isEmpty(value:String) : Boolean = value.isEmpty() || value.isBlank()

    private fun verifyPassword(password: String){

        var error = true

        if(isEmpty(password)){
            _signInState.update { currentState ->
                currentState.copy(
                    passwordError = "La password non può essere vuota",
                    isPasswordValid = false
                )
            }
        }else
            error = false


        if(password.length < 6){
            _signInState.update { currentState ->
                currentState.copy(
                    passwordError = "La password deve essere lunga almeno 6 caratteri",
                    isPasswordValid = false
                )
            }
        }else
            error = false


        if(!error)
            _signInState.update { currentState ->
                currentState.copy(
                    passwordError = "",
                    isPasswordValid = true
                )
            }
    }

    fun resetState(){
        _googleSignInState.update { GoogleSignInState() }
    }
}