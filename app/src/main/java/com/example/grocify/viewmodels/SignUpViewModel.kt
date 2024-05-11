package com.example.grocify.viewmodels


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocify.R
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

        val nameStatus = isNotEmpty(name)
        val surnameStatus = isNotEmpty(surname)
        val emailStatus = verifyEmail(email)
        val passwordStatus = verifyPassword(password)
        val confirmPasswordStatus = verifyConfirmPassword(password, confirmPassword)

        if(!nameStatus){
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

         if (!surnameStatus){
             _signUpState.update { currentState ->
                 currentState.copy(
                     surnameError = "Il cognome non può essere vuoto",
                     isSurnameValid = false
                 )
             }
         }else
             _signUpState.update { currentState ->
                 currentState.copy(
                     surnameError = "",
                     isSurnameValid = true
                 )
             }

        if(!passwordStatus){
            _signUpState.update { currentState ->
                currentState.copy(
                    passwordError = "Inserisci una password valida (almeno sei caratteri)",
                    isPasswordValid = false
                )
            }
        }else{
            _signUpState.update { currentState ->
                currentState.copy(
                    passwordError = "",
                    isPasswordValid = true
                )
            }
        }

        if(!confirmPasswordStatus){
            _signUpState.update { currentState ->
                currentState.copy(
                    confirmPasswordError = "Le due password non coincidono",
                    isConfirmPasswordValid = false
                )
            }
        }else
            _signUpState.update { currentState ->
                currentState.copy(
                    confirmPasswordError = "",
                    isConfirmPasswordValid = true
                )
            }

        if(!emailStatus){
            _signUpState.update { currentState ->
                currentState.copy(
                    emailError = "Inserisci un email valida",
                    isEmailValid = false
                )
            }
        }else
            _signUpState.update { currentState ->
                currentState.copy(
                    emailError = "",
                    isEmailValid = true
                )
            }

        if(nameStatus && surnameStatus && emailStatus && passwordStatus && confirmPasswordStatus) {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {

                                val newUser = hashMapOf(
                                    "name" to name,
                                    "surname" to surname,
                                    "email" to email,
                                    "password" to password,
                                    "profilePic" to  getApplication<Application>().resources.getString(R.string.default_user_link),
                                    "role" to "user"
                                )

                                db.collection("users")
                                    .add(newUser)
                                    .addOnSuccessListener {
                                        _signUpState.update { currentState ->
                                             currentState.copy(
                                                isSuccessful = true
                                            )
                                        }
                                    }
                            } else {
                                _signUpState.update { currentState ->
                                    currentState.copy(
                                        signUpError = task.exception?.localizedMessage.toString()
                                    )
                                }
                            }
                        }
                    resetState()
                }
            }
        }
    }

    private fun isNotEmpty(value:String) : Boolean = value.isNotEmpty() && value.isNotBlank()

    private fun verifyEmail(email: String): Boolean = isNotEmpty(email) && isEmailValid(email)

    private fun isEmailValid(email: String): Boolean {
        val emailRegex = Regex("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}")
        return emailRegex.matches(email)
    }

    private fun verifyPassword(password: String): Boolean = isNotEmpty(password) && password.length >= 6

    private fun verifyConfirmPassword(password: String, confirmPassword: String): Boolean = password == confirmPassword && isNotEmpty(confirmPassword)

    private fun resetState() = _signUpState.update { SignUpUiState() }
}
