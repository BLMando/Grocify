package com.example.grocify.viewmodels


import android.app.Application
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocify.R
import com.example.grocify.states.SignUpUiState
import com.example.grocify.model.User
import com.example.grocify.utils.isNotEmpty
import com.example.grocify.utils.verifyConfirmPassword
import com.example.grocify.utils.verifyEmail
import com.example.grocify.utils.verifyPassword
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

/**
 * ViewModel for SignUpScreen handling sign-up functionality.
 * @param application - Application context
 */
class SignUpViewModel(application: Application): AndroidViewModel(application){

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    private val _signUpState = MutableStateFlow(SignUpUiState())
    val signUpState: StateFlow<SignUpUiState> = _signUpState.asStateFlow()

    /**
     * Function to handle sign-up process.
     * It performs validation on the input fields and updates the state accordingly.
     * If all validations pass, it creates a new user in the Firebase Authentication and Firestore databases.
     * @param name - Name of the user
     * @param surname - Surname of the user
     * @param email - Email of the user
     * @param password - Password of the user
     * @param confirmPassword - Confirm password of the user
     */
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

                                val newUser = User(
                                    uid = auth.currentUser?.uid,
                                    name = name,
                                    surname = surname,
                                    email = email,
                                    password = password,
                                    profilePic = getApplication<Application>().resources.getString(R.string.default_user_link),
                                    role = ContextCompat.getString(
                                        getApplication(),
                                        R.string.default_user_role
                                    )

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

    private fun resetState() = _signUpState.update { SignUpUiState() }
}
