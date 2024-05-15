package com.example.grocify.viewmodels

import android.app.Application
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocify.R
import com.example.grocify.model.User
import com.example.grocify.data.UserProfileUiState
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.CancellationException

class UserProfileViewModel(application: Application,private val mOneTapClient: SignInClient): AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(UserProfileUiState())
    val uiState:StateFlow<UserProfileUiState> = _uiState.asStateFlow()

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    fun getSignedInUser() {

        val user = auth.currentUser

        if(user?.displayName.isNullOrEmpty()){

            viewModelScope.launch {
                db.collection("users")
                    .whereEqualTo("uid", user?.uid)
                    .get()
                    .addOnSuccessListener { documents ->
                        documents.documents[0].data?.let { document ->
                            val currentUser = User(
                                uid = user?.uid,
                                name = document["name"].toString().replaceFirstChar { it.uppercase() },
                                surname = document["surname"].toString().replaceFirstChar { it.uppercase() },
                                email = user?.email,
                                password = null,
                                profilePic = document["profilePic"].toString(),
                                role = getString(getApplication(), R.string.default_user_role)
                            )
                            _uiState.update { currentState ->
                                currentState.copy(
                                    user = currentUser
                                )
                            }
                        }

                    }
            }
        }else{
            val username = user?.displayName?.split(" ")

            val currentUser = User(
                uid = user?.uid,
                name = username?.get(0).toString().replaceFirstChar { it.uppercase() },
                surname = username?.get(1).toString().replaceFirstChar { it.uppercase() },
                email = user?.email,
                password = null,
                profilePic = user?.photoUrl.toString(),
                role = getString(getApplication(), R.string.default_user_role)
            )

            _uiState.update {
                it.copy(
                    user = currentUser
                )
            }
        }
    }

     fun signOut(){
        viewModelScope.launch {
            try {
                mOneTapClient.signOut()
                auth.signOut()
            }catch(e: Exception){
                e.printStackTrace()
                if(e is CancellationException) throw  e
            }
        }

    }
}