package com.example.grocify.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocify.data.HomeUiState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(application: Application): AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val db = Firebase.firestore
    private val auth = Firebase.auth
    fun getUserRole(){
        viewModelScope.launch {
            db.collection("users")
                .whereEqualTo("email",auth.currentUser?.email)
                .get()
                .addOnSuccessListener { documents ->
                    _uiState.update {
                        it.copy(userRole = documents.documents[0].get("role").toString())
                    }
                }
        }
    }

}