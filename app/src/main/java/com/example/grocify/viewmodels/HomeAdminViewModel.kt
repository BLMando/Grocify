package com.example.grocify.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocify.data.HomeAdminUiState
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

class HomeAdminViewModel(application: Application,private val mOneTapClient: SignInClient): AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(HomeAdminUiState())
    val uiState: StateFlow<HomeAdminUiState> = _uiState.asStateFlow()

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    fun getAllReviews(){
        viewModelScope.launch {
            db.collection("orders_reviews")
                .orderBy("")
        }
    }


    fun getSignedInUserName() {
        val currentUser = auth.currentUser?.uid
        viewModelScope.launch {
            db.collection("users")
                .whereEqualTo("uid", currentUser)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        _uiState.update { currentState ->
                            currentState.copy(currentUserName = document.data["name"].toString().replaceFirstChar { it.uppercase() })
                        }
                    }
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