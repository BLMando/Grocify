package com.example.grocify.viewmodels

import android.app.Application
import android.content.Intent
import android.content.IntentSender
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocify.R
import com.example.grocify.states.GoogleSignInResult
import com.example.grocify.states.GoogleSignInState
import com.example.grocify.states.SignInUiState
import com.example.grocify.model.User
import com.example.grocify.utils.verifyEmail
import com.example.grocify.utils.verifyPassword
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

/**
 * ViewModel class for SignInScreen handling sign-in functionality.
 * @param application The application context.
 * @param mOneTapClient The Google Sign-In client to handle Sign in with Google functionality.
 */
class SignInViewModel(application: Application, private val mOneTapClient: SignInClient): AndroidViewModel(application) {

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    private val _googleSignInState = MutableStateFlow(GoogleSignInState())
    val googleSignInState: StateFlow<GoogleSignInState> = _googleSignInState.asStateFlow()

    private val _signInState = MutableStateFlow(SignInUiState())
    val signInState: StateFlow<SignInUiState> = _signInState.asStateFlow()

    /**
     * Signs in the user with the provided email and password.
     * @param email The email address of the user.
     * @param password The password of the user.
     * This method updates the state flow with the appropriate
     * values for email and password validation and sign-in success or failure.
     */
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
            // Perform sign-in operations in the background
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

    /**
     * This method create the sign-in request and returns the pending intent.
     */
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

    /**
      Signs in a user using a Google Sign-In intent and returns the result.
      Params: intent - The intent containing the Google Sign-In credentials.
      Returns: A GoogleSignInResult object containing either the signed-in user data or an error message.
     */
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

    /**
     * This method handles the sign-in result from the Google Sign-In API.
     * @param result The sign-in result object containing the user data or error message.
     * This method updates the state flow with the appropriate values for sign-in success or failure.
     * @see GoogleSignInResult
     */
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

    /**
     * This method checks if the user is signed in or not.
     */
    fun isUserSignedIn(): Boolean = auth.currentUser != null

    /**
     * This method retrieves the user role from the Firestore database.
     * @return The user role as a string.
     * @see User
     */
    suspend fun getUserRole(): String{
        val res = db.collection("users")
            .whereEqualTo("uid",auth.currentUser?.uid)
            .get().await()

        return res.documents[0].data?.get("role").toString()
    }

    /**
     * This method builds the sign-in request for the Google Sign-In API.
     */
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

    fun resetGoogleState() =  _googleSignInState.update { GoogleSignInState() }
    private fun resetState() = _signInState.update { SignInUiState() }
}