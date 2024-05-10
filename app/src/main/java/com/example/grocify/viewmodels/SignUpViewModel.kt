package com.example.grocify.viewmodels


import android.app.Application
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocify.data.SignUpUiState
import com.google.firebase.auth.FirebaseAuth
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

class SignUpViewModel(application: Application): AndroidViewModel(application){

    private val auth: FirebaseAuth = Firebase.auth
    private val db = Firebase.firestore

    private val _signUpState = MutableStateFlow(SignUpUiState())
    val signUpState: StateFlow<SignUpUiState> = _signUpState.asStateFlow()

    fun signUp(name:String,surname: String,email: String, password: String, confirmPassword: String){

        verifyPassword(password,confirmPassword)
        Log.d("verify","state=${_signUpState.value.passwordError}")
        verifyEmail(email)

        if(isEmpty(name)){
            _signUpState.update { currentState ->
                currentState.copy(
                    nameError = "Il nome non può essere vuoto",
                    isNameValid = false
                )
            }
        }else{
            _signUpState.update { currentState ->
                currentState.copy(
                    nameError = "",
                    isNameValid = true
                )
            }
        }

         if (isEmpty(surname)){
             _signUpState.update { currentState ->
                 currentState.copy(
                     surnameError = "Il cognome non può essere vuoto",
                     isSurnameValid = false
                 )
             }
         }else{
             _signUpState.update { currentState ->
                 currentState.copy(
                     surnameError = "",
                     isSurnameValid = true
                 )
             }
         }

        if(_signUpState.value.isNameValid && _signUpState.value.isSurnameValid && _signUpState.value.isEmailValid && _signUpState.value.isPasswordValid) {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {

                                val newUser = hashMapOf(
                                    "name" to name,
                                    "surname" to surname,
                                    "email" to email,
                                    "password" to password
                                )

                                db.collection("users")
                                    .add(newUser)
                                    .addOnSuccessListener {
                                        _signUpState.update { currentState ->
                                            currentState.copy(
                                                isSuccessful = true,
                                                signUpError = ""
                                            )
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("firestore", "Error adding document", e)
                                    }
                            } else {
                                _signUpState.update { currentState ->
                                    currentState.copy(
                                        isSuccessful = false,
                                        signUpError = task.exception?.message.toString()
                                    )
                                }
                            }
                        }
                }
            }
        }
    }

    private fun isEmpty(value:String) : Boolean = value.isEmpty() || value.isBlank()

    private fun verifyEmail(email: String){

        var error = true

        if(isEmpty(email)){
            _signUpState.update { currentState ->
                currentState.copy(
                    emailError = "L'email non può essere vuota",
                    isEmailValid = false
                )
            }
        }else
            error = false

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            _signUpState.update { currentState ->
                currentState.copy(
                    emailError = "L'email ha un formato non valido",
                    isEmailValid = false
                )
            }
        }else
            error = false

        if(!error)
            _signUpState.update { currentState ->
                currentState.copy(
                    emailError = "",
                    isEmailValid = true
                )
            }
    }

    private fun verifyPassword(password: String, confirmPassword: String){

        var error = true

        if(isEmpty(password) || isEmpty(confirmPassword)){
            _signUpState.update { currentState ->
                currentState.copy(
                    passwordError = "La password non può essere vuota",
                    isPasswordValid = false
                )
            }
        }else
            error = false


        if(password != confirmPassword){
            _signUpState.update { currentState ->
                currentState.copy(
                    passwordError = "Le password non coincidono",
                    isPasswordValid = false
                )
            }
        }else
            error = false

        if(password.length < 6 && confirmPassword.length < 6){
            _signUpState.update { currentState ->
                currentState.copy(
                    passwordError = "La password deve essere lunga almeno 6 caratteri",
                    isPasswordValid = false
                )
            }
        }else
            error = false


        if(!error)
            _signUpState.update { currentState ->
                currentState.copy(
                    passwordError = "",
                    isPasswordValid = true
                )
            }
    }
}
