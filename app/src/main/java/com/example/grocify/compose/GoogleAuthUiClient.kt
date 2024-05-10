package com.example.grocify.compose

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.example.grocify.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException

class GoogleAuthUiClient(
    private val context:Context,
    private val oneTapClient:SignInClient
) {
    private val auth = Firebase.auth

    suspend fun signIn(): IntentSender?{
        val result = try{
            oneTapClient.beginSignIn(
                buildSignInRequest()
            ).await()
        }catch (e: Exception){
            e.printStackTrace()
            if(e is CancellationException) throw  e else null
        }

        return result?.pendingIntent?.intentSender
    }

    suspend fun signInWithIntent(intent: Intent): GoogleSignInResult{
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        val idToken = credential.googleIdToken
        val googleCredential = GoogleAuthProvider.getCredential(idToken,null)
        return try{
            val user = auth.signInWithCredential(googleCredential).await().user
            GoogleSignInResult(
                data = user?.run {
                    UserData(
                        username = displayName,
                        email = email,
                        profilePic = photoUrl?.toString()
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

    suspend fun signOut(){
        try {
            oneTapClient.signOut().await()
            auth.signOut()
        }catch(e: Exception){
            e.printStackTrace()
            if(e is CancellationException) throw  e
        }
    }

    fun getSignedInUser(): UserData? =  auth.currentUser?.run {
        UserData(
            username = displayName,
            email = email,
            profilePic = photoUrl?.toString()
        )
    }


    private fun buildSignInRequest():BeginSignInRequest{
        return BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(context.getString(R.string.client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }
}