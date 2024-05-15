package com.example.grocify.viewmodels

import android.app.Application
import android.content.Intent
import android.content.IntentSender
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocify.R
import com.example.grocify.data.GoogleSignInResult
import com.example.grocify.data.GoogleSignInState
import com.example.grocify.data.SignInUiState
import com.example.grocify.model.User
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.concurrent.CancellationException

class SignInViewModel(application: Application, private val mOneTapClient: SignInClient): AndroidViewModel(application) {

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    private val _googleSignInState = MutableStateFlow(GoogleSignInState())
    val googleSignInState: StateFlow<GoogleSignInState> = _googleSignInState.asStateFlow()

    private val _signInState = MutableStateFlow(SignInUiState())
    val signInState: StateFlow<SignInUiState> = _signInState.asStateFlow()

    fun signInWithCredentials(email: String, password: String) {

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

    suspend fun signInWithGoogle(): IntentSender?{
        val result = try{
            mOneTapClient.beginSignIn(
                buildSignInRequest()
            ).await()
        }catch (e: Exception){
            e.printStackTrace()
            if(e is CancellationException) throw  e else null
        }

        return result?.pendingIntent?.intentSender
    }

    suspend fun signInWithIntent(intent: Intent): GoogleSignInResult {
        val credential = mOneTapClient.getSignInCredentialFromIntent(intent)
        val idToken = credential.googleIdToken
        val googleCredential = GoogleAuthProvider.getCredential(idToken,null)
        return try{
            val user = auth.signInWithCredential(googleCredential).await().user
            GoogleSignInResult(
                data = user?.run {
                    val username = displayName?.split(" ")
                    User(
                        uid = uid,
                        name = username?.get(0),
                        surname = username?.get(1),
                        email = email,
                        password = null,
                        profilePic = photoUrl?.toString(),
                        role = getString(getApplication(),R.string.default_user_role)
                    )
                },
                error = null
            )
        }catch(e: Exception){
            e.printStackTrace()
            if(e is CancellationException) throw  e
            GoogleSignInResult(
                data = null,
                error = e.message
            )
        }
    }

    fun onSignInResult(result: GoogleSignInResult){
        if(result.data != null){
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    result.data.let { user ->
                        db.collection("users")
                            .whereEqualTo("uid", user.uid)
                            .get()
                            .addOnSuccessListener { documents ->
                                if (documents.isEmpty)
                                    db.collection("users")
                                        .add(user)
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

    fun getSignedInUser(): Boolean = auth.currentUser != null

    suspend fun getUserRole(): String{
            val res = db.collection("users")
                .whereEqualTo("uid",auth.currentUser?.uid)
                .get().await()

            return res.documents[0].data?.get("role").toString()
    }

    private fun buildSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(getApplication(),R.string.client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
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